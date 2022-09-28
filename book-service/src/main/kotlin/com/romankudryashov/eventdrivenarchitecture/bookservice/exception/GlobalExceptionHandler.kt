// TODO: use the same format of responses for all exceptions: https://github.com/spring-projects/spring-boot/issues/33885
// TODO: use RFC 9457 error response format (see https://github.com/spring-projects/spring-boot/issues/19525)
// TODO: no need to print an exception stack trace explicitly (`spring.mvc.log-resolved-exception` not working)
package com.romankudryashov.eventdrivenarchitecture.bookservice.exception

import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import jakarta.validation.ConstraintViolationException

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(BookServiceException::class)
    fun handleBookServiceException(exception: BookServiceException, request: WebRequest): ErrorResponse {
        log.error("An exception was handled:", exception)
        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.message!!).build()
    }

    @ExceptionHandler(AccessRestrictedException::class)
    fun handleAccessRestrictedException(exception: AccessRestrictedException, request: WebRequest): ErrorResponse {
        log.error("An exception was handled:", exception)
        return ErrorResponse.builder(exception, HttpStatus.FORBIDDEN, exception.message!!).build()
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: NotFoundException, request: WebRequest): ErrorResponse {
        log.error("An exception was handled:", exception)
        return ErrorResponse.builder(exception, HttpStatus.NOT_FOUND, exception.message!!).build()
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(exception: ConstraintViolationException, request: WebRequest): ErrorResponse {
        log.error("An exception was handled:", exception)
        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, "Some data is not valid").apply {
            exception.constraintViolations.forEach {
                this.detail("'${it.propertyPath}': " + it.message)
            }
        }.build()
    }

    @ExceptionHandler(PSQLException::class)
    fun handlePSQLException(exception: PSQLException, request: WebRequest): ErrorResponse {
        log.error("An exception was handled:", exception)
        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, "Database access error. Please contact a system administrator").build()
    }
}
