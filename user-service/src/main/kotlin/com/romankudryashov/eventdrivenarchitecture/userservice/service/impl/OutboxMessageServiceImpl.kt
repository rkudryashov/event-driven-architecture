package com.romankudryashov.eventdrivenarchitecture.userservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.commonmodel.AggregateType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.RollbackBookLentCommand
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.SendNotificationCommand
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification
import com.romankudryashov.eventdrivenarchitecture.userservice.exception.UserServiceException
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.OutboxMessageRepository
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.OutboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.userservice.service.OutboxMessageService
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

    private val outboxEventTypeToTopic = mapOf(
        SendNotificationCommand to "notifications",
        RollbackBookLentCommand to "rollback"
    )

    override fun saveRollbackBookLentCommandMessage(payload: Book) = save(createOutboxMessage(AggregateType.Book, payload.id, RollbackBookLentCommand, payload))

    override fun saveSendNotificationCommandMessage(payload: Notification, aggregateId: Long) =
        save(createOutboxMessage(AggregateType.Notification, null, SendNotificationCommand, payload))

    private fun <T> createOutboxMessage(aggregateType: AggregateType, aggregateId: Long?, type: EventType, payload: T) = OutboxMessageEntity(
        aggregateType = aggregateType,
        aggregateId = aggregateId,
        type = type,
        topic = outboxEventTypeToTopic[type] ?: throw UserServiceException("Can't determine topic for outbox event type `$type`"),
        payload = objectMapper.convertValue(payload, JsonNode::class.java)
    )

    private fun save(outboxMessage: OutboxMessageEntity) {
        log.debug("Start saving an outbox message: {}", outboxMessage)
        outboxMessageRepository.save(outboxMessage)
        outboxMessageRepository.deleteById(outboxMessage.id!!)
    }
}
