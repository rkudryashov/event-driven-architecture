package com.romankudryashov.eventdrivenarchitecture.bookservice.api

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.controller.AuthorsApiDelegate
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Author
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.AuthorToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.AuthorService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class AuthorsApiDelegateImpl(
    private val authorService: AuthorService
) : AuthorsApiDelegate {

    override fun getAuthors(): ResponseEntity<List<Author>> = ResponseEntity.ok(authorService.getAll())

    override fun updateAuthor(id: Long, authorToSave: AuthorToSave): ResponseEntity<Author> {
        val updatedAuthor = authorService.update(id, authorToSave)
        return ResponseEntity.ok(updatedAuthor)
    }
}
