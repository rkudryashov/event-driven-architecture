package com.romankudryashov.eventdrivenarchitecture.notificationservice.service

interface EmailService {
    fun send(to: String, subject: String, text: String)
}
