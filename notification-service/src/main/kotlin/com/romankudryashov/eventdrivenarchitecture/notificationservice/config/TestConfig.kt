package com.romankudryashov.eventdrivenarchitecture.notificationservice.config

import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.EmailService
import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.impl.EmailServiceTestImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender

@Configuration
@Profile("test")
class TestConfig(
    @Value("\${spring.mail.username}")
    private val emailFrom: String
) {

    @Bean
    @Primary
    fun emailService(emailSender: JavaMailSender): EmailService = EmailServiceTestImpl(emailSender, emailFrom)
}
