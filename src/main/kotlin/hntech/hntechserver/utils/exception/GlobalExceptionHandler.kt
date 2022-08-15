package hntech.hntechserver.utils.exception

import hntech.hntechserver.utils.ErrorListResponse
import hntech.hntechserver.utils.ErrorResponse
import hntech.hntechserver.utils.badRequest
import hntech.hntechserver.utils.logging.logger
import hntech.hntechserver.utils.unauthorized
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.security.auth.login.LoginException

@RestControllerAdvice
class GlobalExceptionHandler {

    val log = logger()

    @ExceptionHandler(Exception::class)
    fun globalErrorHandle(ex: Exception): ResponseEntity<ErrorResponse> {
        log.warn("[{}] handled: {}", ex.javaClass.simpleName, ex.message)
        return badRequest(ex)
    }

    @ExceptionHandler(ValidationException::class)
    fun validationErrorHandle(ex: ValidationException): ResponseEntity<ErrorListResponse> {
        log.warn("[{}] : {}, \nerrors = {}", ex.javaClass.simpleName, ex.message, ex.bindingResult.fieldErrors)
        return badRequest(ex.bindingResult)
    }

    @ExceptionHandler(LoginException::class)
    fun loginExceptionHandle(ex: LoginException): ResponseEntity<ErrorResponse> {
        log.warn("[LoginException] : {}", ex.message)
        return unauthorized(ex)
    }

}