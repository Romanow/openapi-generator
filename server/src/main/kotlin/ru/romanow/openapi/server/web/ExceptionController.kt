package ru.romanow.openapi.server.web

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.romanow.openapi.server.model.ErrorDescription
import ru.romanow.openapi.server.model.ErrorResponse
import ru.romanow.openapi.server.model.ValidationErrorResponse

@RestControllerAdvice
class ExceptionController {
    private val logger = LoggerFactory.getLogger(ExceptionController::class.java)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun badRequest(exception: MethodArgumentNotValidException): ValidationErrorResponse {
        val bindingResult = exception.bindingResult
        return ValidationErrorResponse(buildMessage(bindingResult), buildErrors(bindingResult))
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(exception: EntityNotFoundException): ErrorResponse {
        return ErrorResponse(exception.message)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun error(exception: Exception): ErrorResponse {
        logger.error("", exception)
        return ErrorResponse(exception.message)
    }

    private fun buildMessage(bindingResult: BindingResult) =
        "Error on ${bindingResult.target}, rejected errors ${buildAllErrors(bindingResult)}"

    private fun buildAllErrors(bindingResult: BindingResult) =
        bindingResult.allErrors.map { it.defaultMessage }

    private fun buildErrors(bindingResult: BindingResult) =
        bindingResult.fieldErrors.map { ErrorDescription(it.field, it.defaultMessage!!) }
}