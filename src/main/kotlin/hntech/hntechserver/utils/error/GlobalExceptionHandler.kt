package hntech.hntechserver.utils.error


import hntech.hntechserver.file.FileException
import hntech.hntechserver.utils.badRequest
import hntech.hntechserver.utils.internalServerError
import hntech.hntechserver.utils.notFound
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
    = badRequest(ex.message)

    @ExceptionHandler(CategoryException::class)
    fun categoryExceptionHandler(ex: CategoryException): ResponseEntity<String> {
        return when (ex.message) {
            CATEGORY_NOT_FOUND -> notFound(ex.message)
            DUPLICATE_CATEGORY_NAME -> badRequest(ex.message)
            else -> internalServerError(ex.message)
        }
    }

    @ExceptionHandler(ProductException::class)
    fun productExceptionHandler(ex: ProductException): ResponseEntity<String> {
        return when (ex.message) {
            PRODUCT_NOT_FOUND -> notFound(ex.message)
            DUPLICATE_PRODUCT_NAME -> badRequest(ex.message)
            else -> internalServerError(ex.message)
        }
    }
}