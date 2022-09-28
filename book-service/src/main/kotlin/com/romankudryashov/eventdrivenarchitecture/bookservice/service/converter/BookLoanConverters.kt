package com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoan as BookLoanDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.BookLoanToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.BookEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.BookLoanEntity
import com.romankudryashov.eventdrivenarchitecture.commonmodel.BookLoan
import org.springframework.stereotype.Component

@Component
class BookLoanEntityToDtoConverter : Converter<BookLoanEntity, BookLoanDto> {

    override fun convert(source: BookLoanEntity): BookLoanDto = BookLoanDto(
        id = source.id,
        userId = source.userId
    )
}

@Component
class BookLoanEntityToModelConverter : Converter<BookLoanEntity, BookLoan> {

    override fun convert(source: BookLoanEntity): BookLoan = BookLoan(
        id = source.id,
        userId = source.userId
    )
}

@Component
class BookLoanToSaveToEntityConverter : Converter<Pair<BookLoanToSave, BookEntity>, BookLoanEntity> {

    override fun convert(source: Pair<BookLoanToSave, BookEntity>): BookLoanEntity = BookLoanEntity(
        book = source.second,
        userId = source.first.userId,
    )
}
