package hntech.hntechserver.common

import hntech.hntechserver.utils.logging.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.security.auth.login.LoginException

@RestControllerAdvice
class GlobalExceptionHandler {

    val log = logger()

    /**
     * @ModelAttribute 에서 바인딩 실패시 발생,
     * 그리고 @Valid 실패시 MethodArgumentNotValidException 이 발생하는데 이 예외는 BindException 이 부모이므로
     * 전부 잡을 수 있음
     */
    @ExceptionHandler(BindException::class)
    fun validationFailHandle(ex: BindException): ResponseEntity<ErrorListResponse> {
        log.warn(
            "[{}] : {}, \nerrors = {}",
            ex.javaClass.simpleName,
            ex.message,
            ex.bindingResult.fieldErrors
        )

        val errors = ex.bindingResult.fieldErrors
        val body = ErrorListResponse(
            errors.map {
                ErrorResponse(VALIDATION_ERROR, it.field + " / " + it.defaultMessage)
            }
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body)
    }

    @ExceptionHandler(Exception::class)
    fun globalErrorHandle(ex: Exception): ResponseEntity<ErrorResponse> {
        log.warn("[{}] handled: {}", ex.javaClass.simpleName, ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))
    }

    @ExceptionHandler(LoginException::class)
    fun loginExceptionHandle(ex: LoginException): ResponseEntity<ErrorResponse> {
        log.warn("[LoginException] : {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(ex.javaClass.simpleName, ex.message!!))
    }

}