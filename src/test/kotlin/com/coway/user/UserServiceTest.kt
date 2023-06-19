package com.coway.user

import com.coway.infra.security.BCryptHashProvider
import com.coway.infra.security.JwtTokenProvider
import com.coway.user.data.entity.User
import com.coway.user.data.dto.UserLoginRequest
import com.coway.user.data.dto.UserRegistrationRequest
import com.coway.user.data.dto.UserUpdateRequest
import com.coway.user.exception.*
import com.coway.user.data.repository.UserRepository
import com.coway.user.service.UserService
import com.coway.user.support.UserFactory
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@QuarkusTest
internal class UserServiceTest {
    @InjectMock
    lateinit var userRepository: UserRepository
    @InjectMock
    lateinit var hashProvider: BCryptHashProvider
    @InjectMock
    lateinit var tokenProvider: JwtTokenProvider

    private lateinit var userService: UserService

    @BeforeEach
    internal fun setUp() {
        userService = UserService(userRepository, hashProvider, tokenProvider)
    }

    @Test
    fun getUser_WhenInvalidUsername_ThenThrowUserNotFoundException() {
        // Given
        val invalidId = "INVALID_ID"
        `when`(userRepository.findById(invalidId)).thenReturn(null)

        // Then
        assertThrows<UserNotFoundException> {
            userService.get(invalidId)
        }
    }

    @Test
    fun register_WhenExistingUsername_ThenThrowUsernameAlreadyExistException() {
        // Given
        val existingUsername = "existingUsername"
        val userRegistrationRequest = UserFactory.create().run {
            UserRegistrationRequest(username = existingUsername, email, password)
        }
        `when`(userRepository.existsUsername(existingUsername)).thenReturn(true)

        // Then
        assertThrows<UsernameAlreadyExistsException> {
            userService.register(userRegistrationRequest)
        }

        verify(userRepository, never()).persist(any<User>())
    }

    @Test
    fun register_WhenExistingEmail_ThenThrowEmailAlreadyExistException() {
        // Given
        val existingEmail = "existing@email.com"
        val userRegistrationRequest = UserFactory.create().run {
            UserRegistrationRequest(username, existingEmail, password)
        }
        `when`(userRepository.existsEmail(existingEmail)).thenReturn(true)

        // Then
        assertThrows<EmailAlreadyExistsException> {
            userService.register(userRegistrationRequest)
        }

        verify(userRepository, never()).persist(any<User>())
    }

    @Test
    fun register_WhenValidRequest_ThenHashPasswordAndGenerateToken() {
        // Given
        val token = "GENERATED_TOKEN"
        val hashedPassword = "HASHED_PASSWORD"
        val validRegistrationRequest = UserFactory.create().run {
            UserRegistrationRequest(username, email, password)
        }

        `when`(userRepository.existsUsername(validRegistrationRequest.username)).thenReturn(false)
        `when`(userRepository.existsEmail(validRegistrationRequest.email)).thenReturn(false)
        `when`(hashProvider.hash(validRegistrationRequest.password)).thenReturn(hashedPassword)
        `when`(tokenProvider.create(any())).thenReturn(token)

        userService.register(validRegistrationRequest)

        // Then
        verify(hashProvider).hash(validRegistrationRequest.password)
        verify(tokenProvider).create(validRegistrationRequest.username)
        verify(userRepository).persist(any<User>())
    }

    @Test
    fun login_WhenInvalidEmail_ThenThrowUnregisteredEmailException() {
        // Given
        val invalidEmail = "INVALID_EMAIL@email.com"
        val userLoginRequest = UserFactory.create().run {
            UserLoginRequest(invalidEmail, password)
        }

        `when`(userRepository.findByEmail(invalidEmail)).thenReturn(null)

        // Then
        assertThrows<UnregisteredEmailException> {
            userService.login(userLoginRequest)
        }
    }

    @Test
    fun login_WhenInvalidPassword_ThenThrowInvalidPasswordException() {
        // Given
        val invalidPassword = "INVALID_PASSWORD"
        val requestedUser = UserFactory.create()
        val userLoginRequest = requestedUser.run {
            UserLoginRequest(email, invalidPassword)
        }

        `when`(userRepository.findByEmail(userLoginRequest.email)).thenReturn(requestedUser)
        `when`(hashProvider.verify(invalidPassword, requestedUser.password)).thenReturn(false)

        // Then
        assertThrows<InvalidPasswordException> {
            userService.login(userLoginRequest)
        }
    }

    @Test
    fun login_WhenValidRequest_ThenGeneratedToken() {
        // Given
        val token = "GENERATED_TOKEN"
        val existingUser = UserFactory.create()
        val userLoginRequest = existingUser.run {
            UserLoginRequest(email, password)
        }

        `when`(userRepository.findByEmail(userLoginRequest.email)).thenReturn(existingUser)
        `when`(hashProvider.verify(userLoginRequest.password, existingUser.password)).thenReturn(true)
        `when`(tokenProvider.create(any())).thenReturn(token)

        userService.login(userLoginRequest)

        // Then
        verify(tokenProvider).create(existingUser.username)
    }

    @Test
    fun update_WhenExistingUsername_ThenThrowUsernameAlreadyExistException() {
        // Given
        val existingUser = UserFactory.create()
        val loggedInUser = UserFactory.create()
        val userUpdateRequest = UserUpdateRequest(username = existingUser.username)

        `when`(userRepository.findById(any())).thenReturn(loggedInUser)
        `when`(userRepository.existsUsername(userUpdateRequest.username!!)).thenReturn(true)

        // Then
        assertThrows<UsernameAlreadyExistsException> {
            userService.update(loggedInUser.username, userUpdateRequest)
        }

        verify(userRepository, never()).persist(any<User>())
    }

    @Test
    fun update_WhenExistingEmail_ThenThrowEmailAlreadyExistException() {
        // Given
        val existingUser = UserFactory.create()
        val loggedInUser = UserFactory.create()
        val userUpdateRequest = UserUpdateRequest(email = existingUser.email)

        `when`(userRepository.findById(any())).thenReturn(loggedInUser)
        `when`(userRepository.existsEmail(userUpdateRequest.email!!)).thenReturn(true)

        // Then
        assertThrows<EmailAlreadyExistsException> {
            userService.update(loggedInUser.username, userUpdateRequest)
        }

        verify(userRepository, never()).persist(any<User>())
    }
}