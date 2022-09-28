package com.romankudryashov.eventdrivenarchitecture.bookservice.service

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Author as AuthorDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.AuthorToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.AuthorEntity

interface AuthorService {

    fun getAll(): List<AuthorDto>

    fun getEntityById(id: Long): AuthorEntity?

    fun update(id: Long, author: AuthorToSave): AuthorDto
}
