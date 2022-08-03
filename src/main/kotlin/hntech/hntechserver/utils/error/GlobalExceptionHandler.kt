package hntech.hntechserver.utils.error


import hntech.hntechserver.file.FileException
import hntech.hntechserver.utils.ErrorResponse
import hntech.hntechserver.utils.badRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(FileException::class)
    fun fileUploadErrorHandle(ex: FileException) = badRequest(ex)

    @ExceptionHandler(ValidationException::class)
    fun validationErrorHandle(ex: ValidationException) = badRequest(ex.bindingResult)

    @ExceptionHandler(NoSuchElementException::class)
    fun notFoundErrorHandle(ex: NoSuchElementException) = badRequest(ex)

    @ExceptionHandler(CategoryException::class)
    fun categoryExceptionHandler(ex: CategoryException) =
        badRequest(ex)

    @ExceptionHandler(ProductException::class)
    fun productExceptionHandler(ex: ProductException) =
        badRequest(ex)

}