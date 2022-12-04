package com.romankudryashov.eventdrivenarchitecture.notificationservice.service

import com.romankudryashov.eventdrivenarchitecture.notificationservice.persistence.entity.InboxMessageEntity

interface InboxMessageService {

    fun markInboxMessagesAsReadyForProcessingByInstance(batchSize: Int): Int

    fun getBatchForProcessing(batchSize: Int): List<InboxMessageEntity>

    fun process(inboxMessage: InboxMessageEntity)
}
