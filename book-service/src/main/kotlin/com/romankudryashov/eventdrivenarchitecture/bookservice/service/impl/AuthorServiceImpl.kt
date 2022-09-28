package com.romankudryashov.eventdrivenarchitecture.bookservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.Author as AuthorDto
import com.romankudryashov.eventdrivenarchitecture.bookservice.api.model.AuthorToSave
import com.romankudryashov.eventdrivenarchitecture.bookservice.exception.NotFoundException
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.AuthorRepository
import com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity.AuthorEntity
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.AuthorService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.OutboxMessageService
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.AuthorEntityToDtoConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.AuthorEntityToModelConverter
import com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter.AuthorToSaveToEntityConverter
import com.romankudryashov.eventdrivenarchitecture.commonmodel.CurrentAndPreviousState
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Primary
class AuthorServiceImpl(
    private val outboxMessageService: OutboxMessageService,
    private val authorRepository: AuthorRepository,
    private val authorEntityToDtoConverter: AuthorEntityToDtoConverter,
    private val authorEntityToModelConverter: AuthorEntityToModelConverter,
    private val authorToSaveToEntityConverter: AuthorToSaveToEntityConverter
) : AuthorService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun getAll(): List<AuthorDto> = authorRepository.findAllByOrderByIdAsc()
        .map { authorEntityToDtoConverter.convert(it) }

    override fun getEntityById(id: Long): AuthorEntity? = authorRepository.findByIdOrNull(id)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun update(id: Long, author: AuthorToSave): AuthorDto {
        log.debug("Start update an author: id={}, new state={}", id, author)

        val existingAuthor = authorRepository.findByIdOrNull(id) ?: throw NotFoundException("Author", id)
        val existingAuthorModel = authorEntityToModelConverter.convert(existingAuthor)
        val authorToUpdate = authorToSaveToEntityConverter.convert(Pair(author, existingAuthor))
        val updatedAuthor = authorRepository.save(authorToUpdate)

        val updatedAuthorModel = authorEntityToModelConverter.convert(updatedAuthor)
        outboxMessageService.saveAuthorChangedEventMessage(CurrentAndPreviousState(updatedAuthorModel, existingAuthorModel))

        return authorEntityToDtoConverter.convert(updatedAuthor)
    }
}
