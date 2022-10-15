package com.romankudryashov.eventdrivenarchitecture.userservice.service

import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.UserEntity

interface UserService {

    fun getAll(): List<UserEntity>

    fun getById(id: Long): UserEntity?
}
