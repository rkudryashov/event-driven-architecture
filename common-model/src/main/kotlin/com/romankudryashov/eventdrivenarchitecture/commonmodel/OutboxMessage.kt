package com.romankudryashov.eventdrivenarchitecture.commonmodel

import java.util.UUID

// this class is used for direct writes of outbox messages to the WAL
data class OutboxMessage<T>(
    val id: UUID = UUID.randomUUID(),
    val aggregateType: AggregateType,
    val aggregateId: Long?,
    val type: EventType,
    val topic: String,
    val payload: T
)
