package com.romankudryashov.eventdrivenarchitecture.userservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.AuthorChanged
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookChanged
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookCreated
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookDeleted
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookLent
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookLoanCanceled
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookReturned
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification.Channel.Email
import com.romankudryashov.eventdrivenarchitecture.userservice.exception.UserServiceException
import com.romankudryashov.eventdrivenarchitecture.userservice.model.BaseNotificationMessageParams
import com.romankudryashov.eventdrivenarchitecture.userservice.model.NotificationMessageParams
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.UserEntity
import com.romankudryashov.eventdrivenarchitecture.userservice.service.NotificationService
import com.romankudryashov.eventdrivenarchitecture.userservice.service.UserService
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.dom.document
import kotlinx.html.dom.serialize
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.style
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationServiceImpl(
    private val userService: UserService
) : NotificationService {

    override fun createNotification(userId: Long, channel: Notification.Channel, baseMessageParams: BaseNotificationMessageParams): Notification {
        val bookUser = userService.getById(userId) ?: createFakeUserForNotificationToAdmin(userId)
        return createNotificationInternal(channel, bookUser, baseMessageParams)
    }

    override fun createNotificationsForAll(channel: Notification.Channel, baseMessageParams: BaseNotificationMessageParams): List<Pair<Notification, Long>> = userService
        .getAll()
        .map { user -> Pair(createNotificationInternal(channel, user, baseMessageParams), user.id) }

    private fun createNotificationInternal(channel: Notification.Channel, user: UserEntity, baseMessageParams: BaseNotificationMessageParams): Notification {
        val recipient = when (channel) {
            Email -> user.email
        }
        val messageParams = NotificationMessageParams(
            baseMessageParams.eventType,
            baseMessageParams.bookName,
            baseMessageParams.delta,
            user.lastName,
            user.firstName,
            user.middleName
        )
        return Notification(channel, recipient, getEmailSubject(messageParams.eventType), getMessage(messageParams), LocalDateTime.now())
    }

    private fun createFakeUserForNotificationToAdmin(userId: Long): UserEntity = UserEntity(
        email = "admin@library.com",
        firstName = "User ID not found: $userId",
        middleName = "",
        lastName = "",
        status = UserEntity.Status.Inactive
    )

    fun getEmailSubject(eventType: EventType) = when (eventType) {
        BookCreated -> "A new book was added"
        BookChanged -> "Changes in your book"
        BookDeleted -> "A book was deleted"
        BookLent -> "You've borrowed a book"
        BookLoanCanceled -> "Your book loan has been canceled"
        BookReturned -> "You've returned a book"
        AuthorChanged -> "Changes in the data of the author of your book"
        else -> throw UserServiceException("Event type $eventType can't be processed")
    }

    fun getMessage(notificationMessageParams: NotificationMessageParams): String {
        val descriptionMessage = when (notificationMessageParams.eventType) {
            BookCreated -> "A new book was added to the library:"
            BookChanged -> "There were changes in your book \"${notificationMessageParams.bookName}\". The delta is:"
            BookDeleted -> "The book \"${notificationMessageParams.bookName}\" was deleted:"
            BookLent -> "You've borrowed a book \"${notificationMessageParams.bookName}\""
            BookLoanCanceled -> "Your book loan (\"${notificationMessageParams.bookName}\") has been canceled"
            BookReturned -> "You've returned a book \"${notificationMessageParams.bookName}\""
            AuthorChanged -> "There were changes in the data of the author of your book \"${notificationMessageParams.bookName}\". The delta is:"
            else -> throw UserServiceException("Event type ${notificationMessageParams.eventType} can't be processed")
        }

        val userName = "${notificationMessageParams.userLastName} ${notificationMessageParams.userFirstName} ${notificationMessageParams.userMiddleName}".trim()

        val css = """
            .delta-key {
                font-family: courier new, courier, verdana;
            }

            .delta-current {
                font-family: courier new, courier, verdana;
                background-color: #9af09d;
            }

            .delta-previous {
                font-family: courier new, courier, verdana;
                background-color: #c0c0c0;
                text-decoration: line-through;
            }
        """.trimIndent()

        val document = document {}.apply {
            this.append {
                html {
                    head {
                        style { +css }
                    }

                    body {
                        p { strong { +"Hi $userName," } }

                        p { +descriptionMessage }

                        div {
                            for (deltaItem in notificationMessageParams.delta) {
                                div {
                                    span(classes = "delta-key") { +"${deltaItem.key}: " }

                                    var previousStateExists = false
                                    val previousState = deltaItem.value.second
                                    if (previousState != null) {
                                        previousStateExists = true
                                        getLinesFromNestedObjectState(previousState).forEach { line ->
                                            span(classes = "delta-previous") { +line }
                                            if (previousState is Collection<*>) {
                                                br
                                            }
                                        }
                                    }

                                    val currentState = deltaItem.value.first
                                    if (currentState != null) {
                                        getLinesFromNestedObjectState(currentState).forEach { line ->
                                            val charToPrepend = if (previousStateExists) " " else ""
                                            span(classes = "delta-current") { +"$charToPrepend$line" }
                                            if (currentState is Collection<*>) {
                                                br
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return document.serialize()
    }

    private fun <T> getLinesFromNestedObjectState(deltaItemValue: T): List<String> {
        fun convertNestedValueToString(nestedValue: Any?): String = when (nestedValue) {
            null -> "null"
            is Map<*, *> -> nestedValue.map { (key, value) -> "$key: $value" }.joinToString()
            else -> nestedValue.toString()
        }

        return if (deltaItemValue is Collection<*>) deltaItemValue.mapIndexed { index, element -> "${index + 1}. " + convertNestedValueToString(element) }
        else listOf(convertNestedValueToString(deltaItemValue))
    }
}
