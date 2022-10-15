package com.romankudryashov.eventdrivenarchitecture.userservice.service

import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.InboxMessageEntity

interface InboxMessageService {

    fun markInboxMessagesAsReadyForProcessingByInstance(batchSize: Int): Int

    fun getBatchForProcessing(batchSize: Int): List<InboxMessageEntity>

    fun process(inboxMessage: InboxMessageEntity)
}
