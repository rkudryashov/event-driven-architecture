package com.romankudryashov.eventdrivenarchitecture.notificationservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import jakarta.mail.Message
import jakarta.mail.internet.MimeMessage

class EmailServiceTestImpl(
    private val mailSender: JavaMailSender,
    private val emailFrom: String
) : EmailService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun send(to: String, subject: String, text: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage().apply {
            // use `emailFrom` instead of `to`, that is, an email will be sent to the sender
            setRecipients(Message.RecipientType.TO, emailFrom)
            setSubject(subject)
            setContent(text, "text/html")
        }

        mailSender.send(mimeMessage)

        log.debug("Email has been sent")
    }
}
