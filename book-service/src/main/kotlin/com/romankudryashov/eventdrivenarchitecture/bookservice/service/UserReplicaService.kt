package com.romankudryashov.eventdrivenarchitecture.bookservice.service

import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.UserReplicaEntity

interface UserReplicaService {
    fun getById(id: Long): UserReplicaEntity?
}
