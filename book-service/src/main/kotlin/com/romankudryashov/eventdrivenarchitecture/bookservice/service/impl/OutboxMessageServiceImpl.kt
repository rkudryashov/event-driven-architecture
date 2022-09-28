package com.romankudryashov.eventdrivenarchitecture.bookservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.OutboxMessageRepository
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.OutboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.OutboxMessageService
import com.romankudryashov.eventdrivenarchitecture.commonmodel.AggregateType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Author
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.commonmodel.CurrentAndPreviousState
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.AuthorChanged
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookChanged
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookCreated
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookDeleted
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookLent
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookLoanCanceled
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.BookReturned
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OutboxMessageServiceImpl(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val objectMapper: ObjectMapper
) : OutboxMessageService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun saveBookCreatedEventMessage(payload: Book) = save(createOutboxMessage(AggregateType.Book, payload.id, BookCreated, payload))

    override fun saveBookChangedEventMessage(payload: CurrentAndPreviousState<Book>) =
        save(createOutboxMessage(AggregateType.Book, payload.current.id, BookChanged, payload))

    override fun saveBookDeletedEventMessage(payload: Book) = save(createOutboxMessage(AggregateType.Book, payload.id, BookDeleted, payload))

    override fun saveBookLentEventMessage(payload: Book) = save(createOutboxMessage(AggregateType.Book, payload.id, BookLent, payload))

    override fun saveBookLoanCanceledEventMessage(payload: Book) = save(createOutboxMessage(AggregateType.Book, payload.id, BookLoanCanceled, payload))

    override fun saveBookReturnedEventMessage(payload: Book) = save(createOutboxMessage(AggregateType.Book, payload.id, BookReturned, payload))

    override fun saveAuthorChangedEventMessage(payload: CurrentAndPreviousState<Author>) =
        save(createOutboxMessage(AggregateType.Author, payload.current.id, AuthorChanged, payload))

    private fun <T> createOutboxMessage(aggregateType: AggregateType, aggregateId: Long, type: EventType, payload: T) = OutboxMessageEntity(
        aggregateType = aggregateType,
        aggregateId = aggregateId,
        type = type,
        payload = objectMapper.convertValue(payload, JsonNode::class.java)
    )

    private fun save(outboxMessage: OutboxMessageEntity) {
        log.debug("Start saving an outbox message: {}", outboxMessage)
        outboxMessageRepository.save(outboxMessage)
        outboxMessageRepository.deleteById(outboxMessage.id!!)
    }
}
