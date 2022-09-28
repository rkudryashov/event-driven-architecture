package com.romankudryashov.eventdrivenarchitecture.bookservice.config

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.BooksApiDelegateLimitedImpl
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.controller.BooksApiDelegate
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.AuthorService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.BookService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.AuthorToSaveToEntityConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.AuthorToSaveToEntityLimitedConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.BookToSaveToEntityConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.BookToSaveToEntityLimitedConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestConfig {

    @Bean
    @Primary
    fun booksApiDelegate(bookService: BookService): BooksApiDelegate = BooksApiDelegateLimitedImpl(bookService)

    @Bean
    @Primary
    fun bookToSaveToEntityConverter(authorService: AuthorService): BookToSaveToEntityConverter = BookToSaveToEntityLimitedConverter(authorService)

    @Bean
    @Primary
    fun authorToSaveToEntityConverter(): AuthorToSaveToEntityConverter = AuthorToSaveToEntityLimitedConverter()
}
