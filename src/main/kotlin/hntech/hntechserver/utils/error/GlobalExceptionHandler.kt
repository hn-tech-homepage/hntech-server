package hntech.hntechserver.utils.error

import hntech.hntechserver.file.FileException
import hntech.hntechserver.utils.badRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(FileException::class)
    fun fileUploadErrorHandle(ex: FileException) = badRequest(ex.message)

    @ExceptionHandler(ValidationException::class)
    fun validationErrorHandle(ex: ValidationException)
        = badRequest(ex.bindingResult)

    @ExceptionHandler(NoSuchElementException::class)
    fun notFoundErrorHandle(ex: NoSuchElementException)
        = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
}