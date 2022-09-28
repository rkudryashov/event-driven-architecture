package com.romankudryashov.eventdrivenarchitecture.bookservice.api

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.controller.BooksApiDelegate
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Book
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.BookService
import org.springframework.http.ResponseEntity

class BooksApiDelegateLimitedImpl(
    private val bookService: BookService
) : BooksApiDelegate {

    override fun getBooks(): ResponseEntity<List<Book>> = ResponseEntity.ok(bookService.getAll())

    override fun updateBook(id: Long, bookToSave: BookToSave): ResponseEntity<Book> {
        val updatedBook = bookService.update(id, bookToSave)
        return ResponseEntity.ok(updatedBook)
    }
}
