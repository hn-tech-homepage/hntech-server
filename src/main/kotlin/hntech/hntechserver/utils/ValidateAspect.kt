package hntech.hntechserver.utils

import hntech.hntechserver.exception.ValidationException
import hntech.hntechserver.utils.logging.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.validation.BindingResult

@Aspect
@Component
class ValidateAspect {

    @Around("execution(* hntech.hntechserver.domain..*.*(..))")
    fun validate(joinPoint: ProceedingJoinPoint): Any? {
        for (arg in joinPoint.args) {
            if ((arg is BindingResult) && arg.hasErrors())
                throw ValidationException(arg)
        }
        return joinPoint.proceed()
    }
}