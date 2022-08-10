package hntech.hntechserver.utils.exception

import org.springframework.validation.BindingResult

class ValidationException(var bindingResult: BindingResult): RuntimeException("validation error")