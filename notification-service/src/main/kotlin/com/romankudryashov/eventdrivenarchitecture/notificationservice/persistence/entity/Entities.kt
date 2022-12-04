package com.romankudryashov.eventdrivenarchitecture.notificationservice.persistence.entity

import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "inbox")
class InboxMessageEntity(
    @Id
    val id: UUID,
    val source: String,
    @Enumerated(value = EnumType.STRING)
    val type: EventType,
    @JdbcTypeCode(SqlTypes.JSON)
    val payload: JsonNode,
    @Enumerated(value = EnumType.STRING)
    var status: Status,
    var error: String?,
    var processedBy: String?,
    @Version
    val version: Int
) : AbstractEntity() {

    enum class Status {
        New,
        ReadyForProcessing,
        Completed,
        Error
    }
}
