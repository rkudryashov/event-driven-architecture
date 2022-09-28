package com.romankudryashov.eventdrivenarchitecture.bookservice.service

import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.InboxMessageEntity

interface InboxMessageService {

    fun markInboxMessagesAsReadyForProcessingByInstance(batchSize: Int): Int

    fun getBatchForProcessing(batchSize: Int): List<InboxMessageEntity>

    fun process(inboxMessage: InboxMessageEntity)
}
