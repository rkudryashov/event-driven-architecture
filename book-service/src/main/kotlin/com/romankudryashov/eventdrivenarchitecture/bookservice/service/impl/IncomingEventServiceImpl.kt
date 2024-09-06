package com.romankudryashov.eventdrivenarchitecture.bookservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.bookservice.exception.BookServiceException
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.BookService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.IncomingEventService
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.RollbackBookLentCommand
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class IncomingEventServiceImpl(
    private val bookService: BookService,
    private val objectMapper: ObjectMapper
) : IncomingEventService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun process(eventType: EventType, payload: JsonNode) {
        log.debug("Start processing an incoming event: type={}, payload={}", eventType, payload)

        when (eventType) {
            RollbackBookLentCommand -> processRollbackBookLentCommand(getData(payload))
            else -> throw BookServiceException("Event type $eventType can't be processed")
        }

        log.debug("Event processed")
    }

    private inline fun <reified T> getData(payload: JsonNode): T = objectMapper.treeToValue(payload)

    private fun processRollbackBookLentCommand(book: Book) {
        val bookLoan = book.currentLoan ?: throw BookServiceException("The book: $book doesn't contain loan to cancel")
        bookService.cancelBookLoan(book.id, bookLoan.id)
    }
}
