package com.example.fliplearn_final.util

class Validator<T>(
    private val validationRule: (T) -> Boolean,
    private val errorMessage: String
) {
    fun validate(value: T): ValidationResult {
        return if (validationRule(value)) {
            ValidationResult(true, null)
        } else {
            ValidationResult(false, errorMessage)
        }
    }

    data class ValidationResult(
        val isValid: Boolean,
        val error: String?
    )
}
