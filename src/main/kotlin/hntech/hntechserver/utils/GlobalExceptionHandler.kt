package hntech.hntechserver.utils

import hntech.hntechserver.FileUploadException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadException::class)
    fun fileUploadErrorHandle(ex: FileUploadException) = badRequest(ex.message)


}