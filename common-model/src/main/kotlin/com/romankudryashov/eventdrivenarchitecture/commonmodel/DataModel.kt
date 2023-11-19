package com.romankudryashov.eventdrivenarchitecture.commonmodel

import java.time.LocalDate
import java.time.LocalDateTime

class Book(
    val id: Long,
    val name: String,
    val authors: List<Author>,
    val publicationYear: Int,
    val currentLoan: BookLoan?
)

class Author(
    val id: Long,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val country: Country,
    val dateOfBirth: LocalDate,
    // the initialization of the field helps to avoid an exception during deserialization
    val books: List<Book> = listOf()
)

class BookLoan(
    val id: Long,
    val userId: Long
)

class CurrentAndPreviousState<T>(
    val current: T,
    val previous: T
)

enum class Country {
    Russia
}

class Notification(
    val channel: Channel,
    val recipient: String,
    val subject: String,
    val message: String,
    val createdAt: LocalDateTime
) {

    enum class Channel {
        Email
    }
}
