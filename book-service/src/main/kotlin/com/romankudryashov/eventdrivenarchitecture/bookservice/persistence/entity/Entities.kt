package com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity

import com.romankudryashov.eventdrivenarchitecture.commonmodel.AggregateType
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Country
import com.romankudryashov.eventdrivenarchitecture.commonmodel.EventType
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Generated
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDate
import java.util.UUID
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

@Entity
@Table(name = "book")
class BookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @field:NotBlank
    var name: String,
    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    @field:NotEmpty
    val authors: Set<AuthorEntity>,
    var publicationYear: Int,
    @Enumerated(value = EnumType.STRING)
    var status: Status = Status.Active,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "book_id")
    val loans: MutableSet<BookLoanEntity> = mutableSetOf()
) : AbstractEntity() {

    fun currentLoan(): BookLoanEntity? {
        if (loans.isNotEmpty()) {
            return loans.find { it.status == BookLoanEntity.Status.Active }
        }
        return null
    }

    enum class Status {
        Active,
        Deleted
    }
}

@Entity
@Table(name = "author")
class AuthorEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @field:NotBlank
    var firstName: String,
    @field:NotBlank
    var middleName: String,
    @field:NotBlank
    var lastName: String,
    @Enumerated(value = EnumType.STRING)
    var country: Country,
    var dateOfBirth: LocalDate,
    @ManyToMany(mappedBy = "authors")
    val books: Set<BookEntity> = setOf()
) : AbstractEntity()

@Entity
@Table(name = "book_loan")
class BookLoanEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    val book: BookEntity,
    val userId: Long,
    @Enumerated(value = EnumType.STRING)
    var status: Status = Status.Active
) : AbstractEntity() {

    enum class Status {
        Active,
        Returned,
        Canceled
    }
}

@Entity
@Table(name = "user_replica")
class UserReplicaEntity(
    @Id
    val id: Long,
    @Enumerated(value = EnumType.STRING)
    var status: Status,
) {

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
    val aggregateId: Long,
    @Enumerated(value = EnumType.STRING)
    val type: EventType,
    @JdbcTypeCode(SqlTypes.JSON)
    val payload: JsonNode
) : AbstractEntity()
