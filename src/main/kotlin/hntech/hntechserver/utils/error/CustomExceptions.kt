package hntech.hntechserver.utils.error

import org.springframework.validation.BindingResult

class CategoryException(message: String) : RuntimeException(message)
class ValidationException(var bindingResult: BindingResult): RuntimeException("validation error")