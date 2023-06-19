package com.coway.user.service

import com.coway.infra.security.BCryptHashProvider
import com.coway.infra.security.JwtTokenProvider
import com.coway.user.data.dto.UserLoginRequest
import com.coway.user.data.dto.UserRegistrationRequest
import com.coway.user.data.dto.UserResponse
import com.coway.user.data.dto.UserUpdateRequest
import com.coway.user.exception.*
import com.coway.user.data.repository.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class UserService(
    private val userRepository: UserRepository,
    private val hashProvider: BCryptHashProvider,
    private val tokenProvider: JwtTokenProvider
) {
    fun get(username: String): UserResponse = userRepository.findById(username)?.run {
        UserResponse.build(this, tokenProvider.create(username))
    } ?: throw UserNotFoundException()

    @Transactional
    fun register(newUser: UserRegistrationRequest): UserResponse = newUser.run {
//        if (!username.matches(Patterns.ALPHANUMERICAL))
        if (userRepository.existsUsername(newUser.username)) throw UsernameAlreadyExistsException()
        if (userRepository.existsEmail(newUser.email)) throw EmailAlreadyExistsException()
        UserResponse.build(
            this.toEntity().also {
                it.password = hashProvider.hash(password)
                userRepository.persist(it)
            },
            tokenProvider.create(username)
        )
    }

    fun login(userLoginRequest: UserLoginRequest) = userRepository.findByEmail(userLoginRequest.email)?.run {
        if(!hashProvider.verify(userLoginRequest.password, password)) throw InvalidPasswordException()
        else UserResponse.build(this, tokenProvider.create(username))
    } ?: throw UnregisteredEmailException()

    fun update(loggedInUserId: String, updateRequest: UserUpdateRequest): UserResponse = userRepository
        .findById(loggedInUserId)
        ?.run {
            if (updateRequest.username != null &&
                updateRequest.username != username &&
                userRepository.existsUsername(updateRequest.username)) throw UsernameAlreadyExistsException()

            if(updateRequest.email != null &&
                updateRequest.email != email &&
                userRepository.existsEmail(updateRequest.email)) throw EmailAlreadyExistsException()

            UserResponse.build(
                updateRequest.applyChangesTo(this).apply {
                    if(updateRequest.password != null) this.password = hashProvider.hash(password)
                    userRepository.persist(this)
                },
                tokenProvider.create(username)
            )
        } ?: throw UserNotFoundException()
}