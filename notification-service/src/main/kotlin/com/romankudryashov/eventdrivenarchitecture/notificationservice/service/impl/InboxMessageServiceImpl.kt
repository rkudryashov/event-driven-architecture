package com.romankudryashov.eventdrivenarchitecture.notificationservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.notificationservice.exception.NotificationServiceException
import com.romankudryashov.eventdrivenarchitecture.notificationservice.persistence.InboxMessageRepository
import com.romankudryashov.eventdrivenarchitecture.notificationservice.persistence.entity.InboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.InboxMessageService
import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.IncomingEventService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class InboxMessageServiceImpl(
    private val incomingEventService: IncomingEventService,
    private val inboxMessageRepository: InboxMessageRepository,
    @Value("\${spring.application.name}")
    private val applicationName: String
) : InboxMessageService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = [RuntimeException::class])
    override fun markInboxMessagesAsReadyForProcessingByInstance(batchSize: Int): Int {
        fun saveReadyForProcessing(inboxMessage: InboxMessageEntity) {
            log.debug("Start saving a message ready for processing, id={}", inboxMessage.id)

            if (inboxMessage.status != InboxMessageEntity.Status.New) throw NotificationServiceException("Inbox message with id=${inboxMessage.id} is not in 'New' status")

            inboxMessage.status = InboxMessageEntity.Status.ReadyForProcessing
            inboxMessage.processedBy = applicationName

            inboxMessageRepository.save(inboxMessage)
        }

        val newInboxMessages = inboxMessageRepository.findAllByStatusOrderByCreatedAtAsc(InboxMessageEntity.Status.New, PageRequest.of(0, batchSize))

        return if (newInboxMessages.isNotEmpty()) {
            newInboxMessages.forEach { inboxMessage -> saveReadyForProcessing(inboxMessage) }
            newInboxMessages.size
        } else 0
    }

    override fun getBatchForProcessing(batchSize: Int): List<InboxMessageEntity> =
        inboxMessageRepository.findAllByStatusAndProcessedByOrderByCreatedAtAsc(InboxMessageEntity.Status.ReadyForProcessing, applicationName, PageRequest.of(0, batchSize))

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = [RuntimeException::class])
    override fun process(inboxMessage: InboxMessageEntity) {
        log.debug("Start processing an inbox message with id={}", inboxMessage.id)

        if (inboxMessage.status != InboxMessageEntity.Status.ReadyForProcessing)
            throw NotificationServiceException("Inbox message with id=${inboxMessage.id} is not in 'ReadyForProcessing' status")
        if (inboxMessage.processedBy == null)
            throw NotificationServiceException("'processedBy' field should be set for an inbox message with id=${inboxMessage.id}")

        try {
            incomingEventService.process(inboxMessage.type, inboxMessage.payload)
            inboxMessage.status = InboxMessageEntity.Status.Completed
        } catch (e: Exception) {
            log.error("Exception while processing an incoming event (type=${inboxMessage.type}, payload=${inboxMessage.payload})", e)
            inboxMessage.status = InboxMessageEntity.Status.Error
            inboxMessage.error = e.stackTraceToString()
        }

        inboxMessageRepository.save(inboxMessage)
    }
}
