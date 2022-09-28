package com.romankudryashov.eventdrivenarchitecture.bookservice.service

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Book as BookDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoan as BookLoanDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoanToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookToSave

interface BookService {

    fun getAll(): List<BookDto>

    fun getById(id: Long): BookDto?

    fun create(book: BookToSave): BookDto

    fun update(id: Long, book: BookToSave): BookDto

    fun delete(id: Long)

    fun lendBook(bookId: Long, bookLoan: BookLoanToSave): BookLoanDto

    fun cancelBookLoan(bookId: Long, bookLoanId: Long)

    fun returnBook(bookId: Long, bookLoanId: Long)
}
