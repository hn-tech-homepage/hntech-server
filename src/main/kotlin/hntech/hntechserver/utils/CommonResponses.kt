package hntech.hntechserver.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError


data class BoolResponse(val result: Boolean = true)

data class ErrorResponse(val code: String = "", val message: String = "")

fun convertJson(error: FieldError) = ErrorResponse(VALIDATION_ERROR, error.field + " / " + error.defaultMessage)

fun convertJson(errors: List<FieldError>): ErrorListResponse {
    val result = mutableListOf<ErrorResponse>()
    errors.forEach { result.add(convertJson(it)) }
    return ErrorListResponse(result)
}

data class ErrorListResponse(val errors: List<ErrorResponse>)

/**
 * REST 응답 관련 메소드
 */
fun <T> badRequest(body: T) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)

fun badRequest(bindingResult: BindingResult) =
    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(convertJson(bindingResult.fieldErrors))

fun success() = ResponseEntity.status(HttpStatus.OK).body(BoolResponse(true))

fun <T> success(body: T) = ResponseEntity.status(HttpStatus.OK).body(body)

