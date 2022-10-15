package com.romankudryashov.eventdrivenarchitecture.userservice.service

import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification
import com.romankudryashov.eventdrivenarchitecture.userservice.model.BaseNotificationMessageParams

interface NotificationService {

    fun createNotification(userId: Long, channel: Notification.Channel, baseMessageParams: BaseNotificationMessageParams): Notification

    fun createNotificationsForAll(channel: Notification.Channel, baseMessageParams: BaseNotificationMessageParams): List<Pair<Notification, Long>>
}
