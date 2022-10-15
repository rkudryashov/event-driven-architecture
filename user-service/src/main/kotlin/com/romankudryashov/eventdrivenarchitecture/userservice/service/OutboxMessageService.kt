package com.romankudryashov.eventdrivenarchitecture.userservice.service

import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification

interface OutboxMessageService {

    fun saveSendNotificationCommandMessage(payload: Notification, aggregateId: Long)

    fun saveRollbackBookLentCommandMessage(payload: Book)
}
