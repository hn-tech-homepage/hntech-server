package hntech.hntechserver.utils.error

import org.springframework.validation.BindingResult

// 카테고리
const val CATEGORY_NOT_FOUND = "해당 카테고리가 존재하지 않습니다."
const val DUPLICATE_CATEGORY_NAME = "해당 이름의 카테고리가 이미 존재합니다."

// 제품
const val PRODUCT_NOT_FOUND = "해당 제품이 존재하지 않습니다."
const val DUPLICATE_PRODUCT_NAME = "해당 이름의 제품이 이미 존재합니다."

class CategoryException(message: String) : RuntimeException(message)
class ProductException(message: String): RuntimeException(message)
class FileException(message: String) : RuntimeException(message)
class ValidationException(var bindingResult: BindingResult): RuntimeException("validation error")