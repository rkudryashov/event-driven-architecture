package com.romankudryashov.eventdrivenarchitecture.userservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.commonmodel.Author
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.commonmodel.CurrentAndPreviousState
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.AuthorChanged
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookChanged
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookCreated
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookDeleted
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookLent
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookLoanCanceled
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookReturned
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification
import com.romankudryashov.eventdrivenarchitecture.userservice.exception.UserServiceException
import com.romankudryashov.eventdrivenarchitecture.userservice.model.BaseNotificationMessageParams
import com.romankudryashov.eventdrivenarchitecture.userservice.service.DeltaService
import com.romankudryashov.eventdrivenarchitecture.userservice.service.IncomingEventService
import com.romankudryashov.eventdrivenarchitecture.userservice.service.NotificationService
import com.romankudryashov.eventdrivenarchitecture.userservice.service.OutboxMessageService
import com.romankudryashov.eventdrivenarchitecture.userservice.service.UserService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class IncomingEventServiceImpl(
    private val userService: UserService,
    private val deltaService: DeltaService,
    private val notificationService: NotificationService,
    private val outboxMessageService: OutboxMessageService,
    private val objectMapper: ObjectMapper
) : IncomingEventService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun process(eventType: EventType, payload: JsonNode) {
        log.debug("Start processing an incoming event: type={}, payload={}", eventType, payload)

        when (eventType) {
            BookCreated -> processBookCreatedEvent(getData(payload))
            BookChanged -> processBookChangedEvent(getData(payload))
            BookDeleted -> processBookDeletedEvent(getData(payload))
            BookLent -> processBookLentEvent(getData(payload))
            BookLoanCanceled -> processBookLoanCanceledEvent(getData(payload))
            BookReturned -> processBookReturnedEvent(getData(payload))
            AuthorChanged -> processAuthorChangedEvent(getData(payload))
            else -> throw UserServiceException("Event type $eventType can't be processed")
        }
    }

    private inline fun <reified T> getData(payload: JsonNode): T = objectMapper.treeToValue(payload)

    private fun processBookCreatedEvent(book: Book) {
        val bookData = deltaService.getDelta(book, null)
        val notifications = notificationService.createNotificationsForAll(
            Notification.Channel.Email,
            BaseNotificationMessageParams(BookCreated, book.name, bookData)
        )
        notifications.forEach { outboxMessageService.saveSendNotificationCommandMessage(it.first, it.second) }
    }

    private fun processBookChangedEvent(currentAndPreviousState: CurrentAndPreviousState<Book>) {
        val oldBook = currentAndPreviousState.previous
        val newBook = currentAndPreviousState.current
        val currentLoan = newBook.currentLoan
        if (currentLoan != null) {
            val bookDelta = deltaService.getDelta(newBook, oldBook)
            val notification = notificationService.createNotification(
                currentLoan.userId,
                Notification.Channel.Email,
                BaseNotificationMessageParams(BookChanged, newBook.name, bookDelta)
            )
            outboxMessageService.saveSendNotificationCommandMessage(notification, currentLoan.userId)
        }
    }

    private fun processBookDeletedEvent(book: Book) {
        val bookData = deltaService.getDelta(null, book)
        val notifications = notificationService.createNotificationsForAll(
            Notification.Channel.Email,
            BaseNotificationMessageParams(BookDeleted, book.name, bookData)
        )
        notifications.forEach { outboxMessageService.saveSendNotificationCommandMessage(it.first, it.second) }
    }

    private fun processBookLentEvent(book: Book) {
        val bookLoan = book.currentLoan ?: throw UserServiceException("The lent book: $book doesn't contain current loan")
        val user = userService.getById(bookLoan.userId)
        // book can't be borrowed because the user doesn't exist
        if (user == null) {
            outboxMessageService.saveRollbackBookLentCommandMessage(book)
        } else {
            val bookData = deltaService.getDelta(book, null)
            val notification = notificationService.createNotification(
                user.id,
                Notification.Channel.Email,
                BaseNotificationMessageParams(BookLent, book.name, bookData)
            )
            outboxMessageService.saveSendNotificationCommandMessage(notification, user.id)
        }
    }

    private fun processBookLoanCanceledEvent(book: Book) {
        val bookLoan = book.currentLoan ?: throw UserServiceException("The book: $book doesn't contain canceled loan")
        val bookData = deltaService.getDelta(book, null)
        val notification = notificationService.createNotification(
            bookLoan.userId,
            Notification.Channel.Email,
            BaseNotificationMessageParams(BookLoanCanceled, book.name, bookData)
        )
        outboxMessageService.saveSendNotificationCommandMessage(notification, bookLoan.userId)
    }

    private fun processBookReturnedEvent(book: Book) {
        val bookLoan = book.currentLoan ?: throw UserServiceException("The returned book: $book doesn't contain previous loan")
        val bookData = deltaService.getDelta(book, null)
        val notification = notificationService.createNotification(
            bookLoan.userId,
            Notification.Channel.Email,
            BaseNotificationMessageParams(BookReturned, book.name, bookData)
        )
        outboxMessageService.saveSendNotificationCommandMessage(notification, bookLoan.userId)
    }

    private fun processAuthorChangedEvent(currentAndPreviousState: CurrentAndPreviousState<Author>) {
        val oldAuthor = currentAndPreviousState.previous
        val newAuthor = currentAndPreviousState.current
        if (newAuthor.books.isNotEmpty()) {
            val authorDelta = deltaService.getDelta(newAuthor, oldAuthor)
            newAuthor.books.forEach { book ->
                val currentLoan = book.currentLoan
                if (currentLoan != null) {
                    val notification = notificationService.createNotification(
                        currentLoan.userId,
                        Notification.Channel.Email,
                        BaseNotificationMessageParams(AuthorChanged, book.name, authorDelta)
                    )
                    outboxMessageService.saveSendNotificationCommandMessage(notification, currentLoan.userId)
                }
            }
        }
    }
}
