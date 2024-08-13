package com.romankudryashov.eventdrivenarchitecture.userservice.persistence

import com.romankudryashov.eventdrivenarchitecture.commonmodel.OutboxMessage
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.InboxMessageEntity
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.UserEntity
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import java.util.UUID
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findAllByStatus(status: UserEntity.Status): List<UserEntity>
}

interface InboxMessageRepository : JpaRepository<InboxMessageEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAllByStatusOrderByCreatedAtAsc(status: InboxMessageEntity.Status, pageable: Pageable): List<InboxMessageEntity>

    fun findAllByStatusAndProcessedByOrderByCreatedAtAsc(status: InboxMessageEntity.Status, processedBy: String, pageable: Pageable): List<InboxMessageEntity>
}

@Repository
class OutboxMessageRepository(
    private val entityManager: EntityManager,
    private val objectMapper: ObjectMapper
) {
    fun <T> writeOutboxMessageToWalInsideTransaction(outboxMessage: OutboxMessage<T>) {
        val outboxMessageJson = objectMapper.writeValueAsString(outboxMessage)
        entityManager.createQuery("SELECT pg_logical_emit_message(true, 'outbox', :outboxMessage)")
            .setParameter("outboxMessage", outboxMessageJson)
            .singleResult
    }
}
