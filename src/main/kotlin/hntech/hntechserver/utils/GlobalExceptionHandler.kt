package hntech.hntechserver.utils

import hntech.hntechserver.FileException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(FileException::class)
    fun fileUploadErrorHandle(ex: FileException) = badRequest(ex.message)


}