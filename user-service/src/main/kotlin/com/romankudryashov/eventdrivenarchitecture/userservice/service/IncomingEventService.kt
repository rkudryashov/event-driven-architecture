package com.romankudryashov.eventdrivenarchitecture.userservice.service

import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.fasterxml.jackson.databind.JsonNode

interface IncomingEventService {
    fun process(eventType: EventType, payload: JsonNode)
}