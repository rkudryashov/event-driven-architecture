package com.romankudryashov.eventdrivenarchitecture.bookservice.service

import com.romankudryashov.eventdrivenarchitecture.commonmodel.Author
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.commonmodel.CurrentAndPreviousState

interface OutboxMessageService {

    fun saveBookCreatedEventMessage(payload: Book)

    fun saveBookChangedEventMessage(payload: CurrentAndPreviousState<Book>)

    fun saveBookDeletedEventMessage(payload: Book)

    fun saveBookLentEventMessage(payload: Book)

    fun saveBookLoanCanceledEventMessage(payload: Book)

    fun saveBookReturnedEventMessage(payload: Book)

    fun saveAuthorChangedEventMessage(payload: CurrentAndPreviousState<Author>)
}
