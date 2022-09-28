package com.romankudryashov.eventdrivenarchitecture.bookservice.api

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.controller.BooksApiDelegate
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Book
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoan
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoanToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class BooksApiDelegateImpl(
    private val bookService: BookService
) : BooksApiDelegate {

    override fun getBooks(): ResponseEntity<List<Book>> = ResponseEntity.ok(bookService.getAll())

    override fun createBook(bookToSave: BookToSave): ResponseEntity<Book> {
        val createdBook = bookService.create(bookToSave)
        return ResponseEntity.ok(createdBook)
    }

    override fun updateBook(id: Long, bookToSave: BookToSave): ResponseEntity<Book> {
        val updatedBook = bookService.update(id, bookToSave)
        return ResponseEntity.ok(updatedBook)
    }

    override fun deleteBook(id: Long): ResponseEntity<Unit> {
        bookService.delete(id)
        return ResponseEntity.noContent().build()
    }

    override fun createBookLoan(bookId: Long, bookLoanToSave: BookLoanToSave): ResponseEntity<BookLoan> {
        val createdBookLoan = bookService.lendBook(bookId, bookLoanToSave)
        return ResponseEntity.ok(createdBookLoan)
    }

    override fun deleteBookLoan(bookId: Long, id: Long): ResponseEntity<Unit> {
        bookService.returnBook(bookId, id)
        return ResponseEntity.noContent().build()
    }
}
