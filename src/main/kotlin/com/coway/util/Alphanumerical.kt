package com.coway.util

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [AlphanumericalValidator::class])
annotation class Alphanumerical(
    val message: String = "Username must consist of alphanumeric characters only",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class AlphanumericalValidator : ConstraintValidator<Alphanumerical, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return value?.matches("[a-zA-Z0-9가-힣-_]+".toRegex()) ?: true
    }
}