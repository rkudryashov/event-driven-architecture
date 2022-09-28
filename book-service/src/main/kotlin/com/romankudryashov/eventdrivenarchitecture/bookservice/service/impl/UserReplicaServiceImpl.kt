package com.romankudryashov.eventdrivenarchitecture.bookservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.UserReplicaRepository
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.UserReplicaEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.UserReplicaService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserReplicaServiceImpl(
    private val userReplicaRepository: UserReplicaRepository
) : UserReplicaService {

    override fun getById(id: Long): UserReplicaEntity? = userReplicaRepository.findByIdOrNull(id)?.let { user ->
        if (user.status == UserReplicaEntity.Status.Active) user
        else null
    }
}
