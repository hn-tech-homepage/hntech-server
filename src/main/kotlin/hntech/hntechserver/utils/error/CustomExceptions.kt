package hntech.hntechserver

import org.springframework.validation.BindingResult

class CategoryException(message: String) : RuntimeException(message)
class FileException(message: String) : RuntimeException(message)
class ValidationException(var bindingResult: BindingResult): RuntimeException("validation error")