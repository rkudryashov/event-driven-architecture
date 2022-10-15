package com.romankudryashov.eventdrivenarchitecture.userservice.service.impl

import com.romankudryashov.eventdrivenarchitecture.commonmodel.Author
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.userservice.exception.UserServiceException
import com.romankudryashov.eventdrivenarchitecture.userservice.service.DeltaService
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Maps
import org.springframework.stereotype.Service

@Service
class DeltaServiceImpl(
    objectMapper: ObjectMapper
) : DeltaService {

    private val deltaObjectMapper = objectMapper.copy().apply {
        configOverride(Book::class.java).setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("id", "currentLoan"))
        configOverride(Author::class.java).setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("id", "books"))
    }

    override fun <T> getDelta(currentObjectState: T?, previousObjectState: T?): Map<String, Pair<Any?, Any?>> {
        if (currentObjectState == null && previousObjectState == null) throw UserServiceException("To retrieve delta, current or previous state should not be null")

        if (currentObjectState == null) return convertToDelta(convertToMap(previousObjectState), false)
        if (previousObjectState == null) return convertToDelta(convertToMap(currentObjectState), true)

        val difference = Maps.difference(convertToMap(currentObjectState), convertToMap(previousObjectState))
        return difference.entriesDiffering().map { (key, value) -> Pair(key, Pair(value.leftValue(), value.rightValue())) }.toMap()
    }

    private fun <T> convertToMap(objectState: T): Map<String, Any?> = deltaObjectMapper.convertValue(objectState, Map::class.java)
        .map { (key, value) -> Pair(key as String, value) }
        .toMap()

    // rename param
    private fun convertToDelta(objectStateMap: Map<String, Any?>, isCurrentState: Boolean): Map<String, Pair<Any?, Any?>> = objectStateMap
        .map { (key, value) ->
            Pair(
                key, Pair(
                    if (isCurrentState) value else null,
                    if (isCurrentState) null else value
                )
            )
        }.toMap()
}
