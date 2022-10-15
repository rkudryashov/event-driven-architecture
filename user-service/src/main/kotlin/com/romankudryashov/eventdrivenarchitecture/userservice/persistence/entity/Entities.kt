package com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity

import com.romankudryashov.eventdrivenarchitecture.commonmodel.AggregateType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Generated
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "library_user")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val email: String,
    @Enumerated(value = EnumType.STRING)
    var status: Status,
) : AbstractEntity() {

    enum class Status {
        Active,
        Inactive
    }
}

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

@Entity
@Table(name = "outbox")
class OutboxMessageEntity(
    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    val id: UUID? = null,
    @Enumerated(value = EnumType.STRING)
    val aggregateType: AggregateType,
    val aggregateId: Long?,
    @Enumerated(value = EnumType.STRING)
    val type: EventType,
    val topic: String,
    @JdbcTypeCode(SqlTypes.JSON)
    val payload: JsonNode
) : AbstractEntity()
