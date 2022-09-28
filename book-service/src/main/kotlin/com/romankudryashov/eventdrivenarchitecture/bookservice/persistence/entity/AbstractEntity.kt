package com.romankudryashov.eventdrivenarchitecture.bookservice.persistence.entity

import org.hibernate.annotations.SourceType
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractEntity(
    @Column(insertable = false, updatable = false)
    val createdAt: ZonedDateTime? = null,
    @Column(insertable = false)
    @UpdateTimestamp(source = SourceType.DB)
    val updatedAt: ZonedDateTime? = null
)
