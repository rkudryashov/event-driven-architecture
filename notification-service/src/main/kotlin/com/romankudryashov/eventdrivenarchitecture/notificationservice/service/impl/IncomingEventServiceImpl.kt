package com.romankudryashov.eventdrivenarchitecture.notificationservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType.SendNotificationCommand
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification
import com.romankudryashov.eventdrivenarchitecture.notificationservice.exception.NotificationServiceException
import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.EmailService
import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.IncomingEventService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class IncomingEventServiceImpl(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper
) : IncomingEventService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun process(eventType: EventType, payload: JsonNode) {
        log.debug("Start processing an incoming event: type={}, payload={}", eventType, payload)

        when (eventType) {
            SendNotificationCommand -> processSendNotificationCommand(getData(payload))
            else -> throw NotificationServiceException("Event type $eventType can't be processed")
        }

        log.debug("Event processed")
    }

    private inline fun <reified T> getData(payload: JsonNode): T = objectMapper.treeToValue(payload)

    private fun processSendNotificationCommand(notification: Notification) {
        when (notification.channel) {
            Notification.Channel.Email -> {
                emailService.send(notification.recipient, notification.subject, notification.message)
            }

            else -> throw NotificationServiceException("Channel is not supported: {${notification.channel.name}}")
        }
    }
}
