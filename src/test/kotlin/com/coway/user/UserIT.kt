package com.coway.user

import com.coway.user.support.UserFactory
import com.coway.util.ValidationMessage.Companion.EMAIL_MUST_BE_NOT_BLANK
import com.coway.util.ValidationMessage.Companion.PASSWORD_MUST_BE_NOT_BLANK
import com.coway.util.ValidationMessage.Companion.USERNAME_MUST_MATCH_PATTERN
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@QuarkusTest
internal class UserIT {

    @Inject
    lateinit var validator: Validator

    @Test
    fun validateUsername_WhenInvalidUsername_ThenReturnConstraintViolation() {
        // Given
        val invalidUsername = "% invalid@Username ?!"

        // When
        val constraintViolations = validator.validate(
            UserFactory.create(username = invalidUsername, email = "valid@email.com")
        )

        // Then
        assertEquals(1, constraintViolations.size)
        assertEquals(
            USERNAME_MUST_MATCH_PATTERN,
            constraintViolations.iterator().next().message
        )
    }

    @Test
    fun validationUsername_WhenBlankUsername_ThenReturnConstraintViolation() {
        // Given
        val blankUsername = ""

        // When
        val constraintViolations = validator.validate(
            UserFactory.create(username = blankUsername, email = "valid@email.com")
        )

        // Then
        assertEquals(1, constraintViolations.size)
        assertEquals(
            USERNAME_MUST_MATCH_PATTERN,
            constraintViolations.iterator().next().message
        )
    }

    @Test
    fun validationEmail_WhenInvalidEmail_ThenReturnConstraintViolation() {
        // Given
        val invalidEmail = "invalid@email@com"

        // When
        val constraintViolation = validator.validate(
            UserFactory.create(email = invalidEmail)
        )

        // Then
        assertEquals(1, constraintViolation.size)
    }

    @Test
    fun validationEmail_WhenBlankEmail_ThenReturnConstraintViolation() {
        // Given
        val blankEmail = ""

        // When
        val constraintViolation = validator.validate(
            UserFactory.create(email = blankEmail)
        )

        // Then
        assertEquals(1, constraintViolation.size)
        assertEquals(EMAIL_MUST_BE_NOT_BLANK, constraintViolation.iterator().next().message)
    }

    @Test
    fun validationPassword_WhenBlankPassword_ThenReturnConstraintViolation() {
        // Given
        val blankPassword = ""

        // When
        val constraintViolation = validator.validate(
            UserFactory.create(password = blankPassword)
        )

        // Then
        assertEquals(1, constraintViolation.size)
        assertEquals(PASSWORD_MUST_BE_NOT_BLANK, constraintViolation.iterator().next().message)
    }
}