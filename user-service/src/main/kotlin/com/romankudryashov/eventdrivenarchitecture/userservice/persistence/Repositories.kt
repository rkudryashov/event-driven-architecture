package com.romankudryashov.eventdrivenarchitecture.userservice.persistence

import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.InboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.OutboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.UserEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.UUID
import jakarta.persistence.LockModeType

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findAllByStatus(status: UserEntity.Status): List<UserEntity>
}

interface InboxMessageRepository : JpaRepository<InboxMessageEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAllByStatusOrderByCreatedAtAsc(status: InboxMessageEntity.Status, pageable: Pageable): List<InboxMessageEntity>

    fun findAllByStatusAndProcessedByOrderByCreatedAtAsc(status: InboxMessageEntity.Status, processedBy: String, pageable: Pageable): List<InboxMessageEntity>
}

interface OutboxMessageRepository : JpaRepository<OutboxMessageEntity, UUID>
