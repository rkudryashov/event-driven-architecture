package com.romankudryashov.eventdrivenarchitecture.bookservice.exception

open class BookServiceException(message: String) : RuntimeException(message)

class AccessRestrictedException : BookServiceException("Access to entity is restricted")

class NotFoundException(entityName: String, entityId: Long) : BookServiceException("Entity '$entityName' not found by id=$entityId")
