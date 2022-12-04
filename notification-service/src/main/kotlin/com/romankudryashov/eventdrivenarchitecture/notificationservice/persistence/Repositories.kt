package com.romankudryashov.eventdrivenarchitecture.notificationservice.persistence

import com.romankudryashov.eventdrivenarchitecture.notificationservice.persistence.entity.InboxMessageEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.UUID
import jakarta.persistence.LockModeType

interface InboxMessageRepository : JpaRepository<InboxMessageEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAllByStatusOrderByCreatedAtAsc(status: InboxMessageEntity.Status, pageable: Pageable): List<InboxMessageEntity>

    fun findAllByStatusAndProcessedByOrderByCreatedAtAsc(status: InboxMessageEntity.Status, processedBy: String, pageable: Pageable): List<InboxMessageEntity>
}
