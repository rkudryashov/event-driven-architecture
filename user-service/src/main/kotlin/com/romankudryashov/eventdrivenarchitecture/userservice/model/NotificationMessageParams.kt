package com.romankudryashov.eventdrivenarchitecture.userservice.model

import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType

open class BaseNotificationMessageParams(
    val eventType: EventType,
    val bookName: String,
    val delta: Map<String, Pair<Any?, Any?>>,
)

class NotificationMessageParams(
    eventType: EventType,
    bookName: String,
    delta: Map<String, Pair<Any?, Any?>>,
    val userLastName: String,
    val userFirstName: String,
    val userMiddleName: String,
) : BaseNotificationMessageParams(eventType, bookName, delta)
