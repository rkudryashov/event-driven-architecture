package com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Book as BookDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.exception.NotFoundException
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.BookEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.AuthorService
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import org.springframework.stereotype.Component

@Component
class BookEntityToDtoConverter(
    private val authorEntityToDtoConverter: AuthorEntityToDtoConverter,
    private val bookLoanEntityToDtoConverter: BookLoanEntityToDtoConverter
) : Converter<BookEntity, BookDto> {

    override fun convert(source: BookEntity): BookDto = BookDto(
        id = source.id,
        name = source.name,
        authors = source.authors.map { authorEntityToDtoConverter.convert(it) },
        publicationYear = source.publicationYear,
        currentLoan = source.currentLoan()?.let { bookLoanEntityToDtoConverter.convert(it) }
    )
}

@Component
class BookEntityToModelConverter(
    private val authorEntityToModelSimpleConverter: AuthorEntityToModelSimpleConverter,
    private val bookLoanEntityToModelConverter: BookLoanEntityToModelConverter
) : Converter<BookEntity, Book> {

    override fun convert(source: BookEntity): Book = Book(
        id = source.id,
        name = source.name,
        authors = source.authors
            .toList()
            .sortedBy { it.id }
            .map { authorEntityToModelSimpleConverter.convert(it) },
        publicationYear = source.publicationYear,
        currentLoan = source.currentLoan()?.let { bookLoanEntityToModelConverter.convert(it) }
    )
}

@Component
class BookToSaveToEntityConverter(
    private val authorService: AuthorService
) : Converter<Pair<BookToSave, BookEntity?>, BookEntity> {

    override fun convert(source: Pair<BookToSave, BookEntity?>): BookEntity {
        val book = source.first
        val existingBookEntity = source.second
        return existingBookEntity?.apply {
            this.name = book.name
            this.publicationYear = book.publicationYear
        } ?: BookEntity(
            name = book.name,
            authors = book.authorIds
                .map { authorId -> authorService.getEntityById(authorId) ?: throw NotFoundException("Author", authorId) }
                .toSet(),
            publicationYear = book.publicationYear
        )
    }
}

class BookToSaveToEntityLimitedConverter(
    authorService: AuthorService
) : BookToSaveToEntityConverter(authorService) {

    override fun convert(source: Pair<BookToSave, BookEntity?>): BookEntity {
        val book = source.first
        val existingBookEntity = source.second
        return existingBookEntity?.apply {
            this.publicationYear = book.publicationYear.coerceIn(1800, 1950)
        }!!
    }
}
