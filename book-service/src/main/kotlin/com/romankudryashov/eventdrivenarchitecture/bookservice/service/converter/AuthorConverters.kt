package com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Author as AuthorDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Country as CountryDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.AuthorToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.AuthorEntity
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Author
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Country
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AuthorEntityToDtoConverter : Converter<AuthorEntity, AuthorDto> {

    override fun convert(source: AuthorEntity): AuthorDto = AuthorDto(
        id = source.id,
        firstName = source.firstName,
        middleName = source.middleName,
        lastName = source.lastName,
        country = CountryDto.valueOf(source.country.name),
        dateOfBirth = source.dateOfBirth
    )
}

@Component
class AuthorEntityToModelConverter(
    private val bookEntityToModelConverter: BookEntityToModelConverter
) : Converter<AuthorEntity, Author> {

    override fun convert(source: AuthorEntity): Author = Author(
        id = source.id,
        firstName = source.firstName,
        middleName = source.middleName,
        lastName = source.lastName,
        country = source.country,
        dateOfBirth = source.dateOfBirth,
        books = source.books
            .toList()
            .sortedBy { it.id }
            .map { bookEntityToModelConverter.convert(it) }
    )
}

@Component
class AuthorEntityToModelSimpleConverter : Converter<AuthorEntity, Author> {

    override fun convert(source: AuthorEntity): Author = Author(
        id = source.id,
        firstName = source.firstName,
        middleName = source.middleName,
        lastName = source.lastName,
        country = source.country,
        dateOfBirth = source.dateOfBirth,
        books = listOf()
    )
}

@Component
class AuthorToSaveToEntityConverter : Converter<Pair<AuthorToSave, AuthorEntity?>, AuthorEntity> {

    override fun convert(source: Pair<AuthorToSave, AuthorEntity?>): AuthorEntity {
        val author = source.first
        val existingAuthorEntity = source.second
        return existingAuthorEntity?.apply {
            this.firstName = author.firstName
            this.middleName = author.middleName
            this.lastName = author.lastName
            this.country = Country.valueOf(author.country.name)
            this.dateOfBirth = author.dateOfBirth
        } ?: AuthorEntity(
            firstName = author.firstName,
            middleName = author.middleName,
            lastName = author.lastName,
            country = Country.valueOf(author.country.name),
            dateOfBirth = author.dateOfBirth
        )
    }
}

class AuthorToSaveToEntityLimitedConverter : AuthorToSaveToEntityConverter() {

    override fun convert(source: Pair<AuthorToSave, AuthorEntity?>): AuthorEntity {
        val author = source.first
        val existingAuthorEntity = source.second
        return existingAuthorEntity?.apply {
            this.dateOfBirth = author.dateOfBirth.coerceIn(LocalDate.ofYearDay(1800, 1), LocalDate.ofYearDay(1950, 1))
        }!!
    }
}
