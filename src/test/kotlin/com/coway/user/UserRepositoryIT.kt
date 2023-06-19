package com.coway.user

import com.coway.user.data.repository.UserRepository
import com.coway.user.support.UserFactory
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@QuarkusTest
internal class UserRepositoryIT {
    @Inject
    lateinit var userRepository: UserRepository

    @Test
    @Transactional
    fun findByEmail_WhenExistingUser_ThenReturnCorrectEntity() {
        // Given
        val existingUser = UserFactory.create().apply(userRepository::persist)

        // When
        val queriedUser = userRepository.findByEmail(existingUser.email)

        // Then
        checkNotNull(queriedUser)
        assertEquals(existingUser.username, queriedUser.username)
        assertEquals(existingUser.email, queriedUser.email)
    }

    @Test
    @Transactional
    fun existsUsername_WhenExistingUser_ThenReturnTrueForExistingUsername() {
        // Given
        val existingUser = UserFactory.create().apply(userRepository::persist)

        // When
        val resultInvalid = userRepository.existsUsername("INVALID_USERNAME")
        val resultExisting = userRepository.existsUsername(existingUser.username)

        // Then
        assertFalse(resultInvalid)
        assertTrue(resultExisting)
    }

    @Test
    @Transactional
    fun existsEmail_WhenExistingUser_ThenReturnTrueForExistingEmail() {
        // Given
        val existingUser = UserFactory.create().apply(userRepository::persist)

        // When
        val resultInvalid = userRepository.existsEmail("INVALID_EMAIL")
        val resultExisting = userRepository.existsEmail(existingUser.email)

        // Then
        assertFalse(resultInvalid)
        assertTrue(resultExisting)
    }
}