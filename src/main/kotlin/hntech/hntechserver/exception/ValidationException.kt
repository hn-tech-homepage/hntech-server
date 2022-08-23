package hntech.hntechserver.exception

import org.springframework.validation.BindingResult

class ValidationException(var bindingResult: BindingResult): RuntimeException("validation error")