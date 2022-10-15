package com.romankudryashov.eventdrivenarchitecture.userservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.UserRepository
import com.romankudryashov.eventdrivenarchitecture.userservice.persistence.entity.UserEntity
import com.romankudryashov.eventdrivenarchitecture.userservice.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getAll(): List<UserEntity> = userRepository.findAllByStatus(UserEntity.Status.Active)

    override fun getById(id: Long): UserEntity? {
        val user = userRepository.findByIdOrNull(id)
        return if (user != null && user.status == UserEntity.Status.Active) {
            user
        } else null
    }

}
