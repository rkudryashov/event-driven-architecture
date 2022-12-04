package com.romankudryashov.eventdrivenarchitecture.notificationservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification
import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmailServiceWebSocketStub(
    private val simpMessagingTemplate: SimpMessagingTemplate
) : EmailService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun send(to: String, subject: String, text: String) {
        // convert back to Notification
        val notification = Notification(Notification.Channel.Email, to, subject, text, LocalDateTime.now())
        simpMessagingTemplate.convertAndSend("/topic/library", notification)
        log.debug("A message over WebSocket has been sent")
    }
}
