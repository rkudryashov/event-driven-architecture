package com.romankudryashov.eventdrivenarchitecture.bookservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Book as BookDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoan as BookLoanDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoanToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.exception.BookServiceException
import com.romankudryashov.eventdrivenarchitecture.bookservice.exception.NotFoundException
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.BookRepository
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.BookEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.BookLoanEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.BookService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.OutboxMessageService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.UserReplicaService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.BookEntityToDtoConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.BookEntityToModelConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.BookLoanEntityToDtoConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.BookLoanToSaveToEntityConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.BookToSaveToEntityConverter
import com.romankudryashov.eventdrivenarchitecture.commonmodel.CurrentAndPreviousState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Primary
class BookServiceImpl(
    private val outboxMessageService: OutboxMessageService,
    private val bookRepository: BookRepository,
    private val bookEntityToDtoConverter: BookEntityToDtoConverter,
    private val bookEntityToModelConverter: BookEntityToModelConverter,
    private val bookToSaveToEntityConverter: BookToSaveToEntityConverter,
    private val bookLoanEntityToDtoConverter: BookLoanEntityToDtoConverter,
    private val bookLoanToSaveToEntityConverter: BookLoanToSaveToEntityConverter,
    private val userReplicaService: UserReplicaService,
    @Value("\${user.check.use-streaming-data}")
    private val useStreamingDataToCheckUser: Boolean
) : BookService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun getAll(): List<BookDto> = bookRepository.findAllByStatusOrderByIdAsc(BookEntity.Status.Active)
        .map { bookEntityToDtoConverter.convert(it) }

    override fun getById(id: Long): BookDto? = getBookEntityById(id)?.let { entity ->
        bookEntityToDtoConverter.convert(entity)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = [Exception::class])
    override fun create(book: BookToSave): BookDto {
        log.debug("Start creating a book: {}", book)

        val bookToCreate = bookToSaveToEntityConverter.convert(Pair(book, null))
        val createdBook = bookRepository.save(bookToCreate)

        val createdBookModel = bookEntityToModelConverter.convert(createdBook)
        outboxMessageService.saveBookCreatedEventMessage(createdBookModel)

        return bookEntityToDtoConverter.convert(createdBook)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = [Exception::class])
    override fun update(id: Long, book: BookToSave): BookDto {
        log.debug("Start updating a book: id={}, new state={}", id, book)

        val existingBook = getBookEntityById(id) ?: throw NotFoundException("Book", id)
        val existingBookModel = bookEntityToModelConverter.convert(existingBook)
        val bookToUpdate = bookToSaveToEntityConverter.convert(Pair(book, existingBook))
        val updatedBook = bookRepository.save(bookToUpdate)

        val updatedBookModel = bookEntityToModelConverter.convert(updatedBook)
        outboxMessageService.saveBookChangedEventMessage(CurrentAndPreviousState(updatedBookModel, existingBookModel))

        return bookEntityToDtoConverter.convert(updatedBook)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = [Exception::class])
    override fun delete(id: Long) {
        log.debug("Start deleting a book: id={}", id)

        val bookToDelete = getBookEntityById(id) ?: throw NotFoundException("Book", id)
        if (bookToDelete.currentLoan() == null) {
            bookToDelete.status = BookEntity.Status.Deleted
            bookRepository.save(bookToDelete)

            val deletedBookModel = bookEntityToModelConverter.convert(bookToDelete)
            outboxMessageService.saveBookDeletedEventMessage(deletedBookModel)
        } else {
            throw BookServiceException("Can't delete a book with id=$id because it is borrowed by a user")
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = [Exception::class])
    override fun lendBook(bookId: Long, bookLoan: BookLoanToSave): BookLoanDto {
        log.debug("Start lending a book: bookId={}, bookLoan={}", bookId, bookLoan)

        val bookToLend = getBookEntityById(bookId) ?: throw NotFoundException("Book", bookId)
        if (bookToLend.currentLoan() != null) throw BookServiceException("The book with id=$bookId is already borrowed by a user")
        if (useStreamingDataToCheckUser) {
            if (userReplicaService.getById(bookLoan.userId) == null) {
                throw NotFoundException("User", bookLoan.userId)
            }
        }

        val bookLoanToCreate = bookLoanToSaveToEntityConverter.convert(Pair(bookLoan, bookToLend))
        bookToLend.loans.add(bookLoanToCreate)
        bookRepository.save(bookToLend)

        val lentBookModel = bookEntityToModelConverter.convert(bookToLend)
        outboxMessageService.saveBookLentEventMessage(lentBookModel)

        return bookLoanEntityToDtoConverter.convert(bookToLend.currentLoan()!!)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = [Exception::class])
    override fun cancelBookLoan(bookId: Long, bookLoanId: Long) {
        log.debug("Start cancelling book loan for a book: bookId={}, bookLoanId={}", bookId, bookLoanId)

        val bookToCancelLoan = getBookEntityById(bookId) ?: throw NotFoundException("Book", bookId)
        val modelOfBookToCancelLoan = bookEntityToModelConverter.convert(bookToCancelLoan)
        val bookLoanToCancel = bookToCancelLoan.loans.find { it.id == bookLoanId } ?: throw NotFoundException("BookLoan", bookLoanId)
        val currentLoan = bookToCancelLoan.currentLoan()
        if (currentLoan == null || bookLoanToCancel.id != currentLoan.id) throw BookServiceException("BookLoan with id=$bookLoanId can't be canceled")
        bookLoanToCancel.status = BookLoanEntity.Status.Canceled
        bookRepository.save(bookToCancelLoan)

        outboxMessageService.saveBookLoanCanceledEventMessage(modelOfBookToCancelLoan)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = [Exception::class])
    override fun returnBook(bookId: Long, bookLoanId: Long) {
        log.debug("Start returning a book: bookId={}, bookLoanId={}", bookId, bookLoanId)

        val bookToReturn = getBookEntityById(bookId) ?: throw NotFoundException("Book", bookId)
        val modelOfBookToReturn = bookEntityToModelConverter.convert(bookToReturn)
        val bookLoanToClose = bookToReturn.loans.find { it.id == bookLoanId } ?: throw NotFoundException("BookLoan", bookLoanId)
        val currentLoan = bookToReturn.currentLoan()
        if (currentLoan == null || bookLoanToClose.id != currentLoan.id) throw BookServiceException("BookLoan with id=$bookLoanId can't be canceled")
        bookLoanToClose.status = BookLoanEntity.Status.Returned
        bookRepository.save(bookToReturn)

        outboxMessageService.saveBookReturnedEventMessage(modelOfBookToReturn)
    }

    private fun getBookEntityById(id: Long): BookEntity? {
        val book = bookRepository.findByIdOrNull(id)
        return if (book != null && book.status != BookEntity.Status.Deleted) {
            book
        } else null
    }
}
