package hntech.hntechserver.utils.error

import hntech.hntechserver.category.CategoryException
import hntech.hntechserver.file.FileException
import hntech.hntechserver.product.ProductException
import hntech.hntechserver.question.CommentException
import hntech.hntechserver.question.QuestionException
import hntech.hntechserver.utils.badRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(FileException::class)
    fun fileUploadErrorHandle(ex: FileException) = badRequest(ex)

    @ExceptionHandler(ValidationException::class)
    fun validationErrorHandle(ex: ValidationException) = badRequest(ex.bindingResult)

    @ExceptionHandler(QuestionException::class)
    fun questionExceptionHandler(ex: QuestionException) = badRequest(ex)

    @ExceptionHandler(CommentException::class)
    fun commentExceptionHandler(ex: CommentException) = badRequest(ex)

    @ExceptionHandler(CategoryException::class)
    fun categoryExceptionHandler(ex: CategoryException) = badRequest(ex)

    @ExceptionHandler(ProductException::class)
    fun productExceptionHandler(ex: ProductException) = badRequest(ex)

}