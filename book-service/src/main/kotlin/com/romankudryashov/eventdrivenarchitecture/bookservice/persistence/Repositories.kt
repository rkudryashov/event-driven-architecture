package com.romankudryashov.eventdrivenarchitecture.bookservice.persistence

import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.AuthorEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.BookEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.InboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.OutboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.UserReplicaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.UUID
import jakarta.persistence.LockModeType

interface BookRepository : JpaRepository<BookEntity, Long> {
    fun findAllByStatusOrderByIdAsc(status: BookEntity.Status): List<BookEntity>
}

interface AuthorRepository : JpaRepository<AuthorEntity, Long> {
    fun findAllByOrderByIdAsc(): List<AuthorEntity>
}

interface UserReplicaRepository : JpaRepository<UserReplicaEntity, Long>

interface InboxMessageRepository : JpaRepository<InboxMessageEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAllByStatusOrderByCreatedAtAsc(status: InboxMessageEntity.Status, pageable: Pageable): List<InboxMessageEntity>

    fun findAllByStatusAndProcessedByOrderByCreatedAtAsc(status: InboxMessageEntity.Status, processedBy: String, pageable: Pageable): List<InboxMessageEntity>
}

interface OutboxMessageRepository : JpaRepository<OutboxMessageEntity, UUID>
