package com.coway.user.data.dto

import com.coway.user.data.entity.User

data class UserUpdateRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
) {
    fun applyChangesTo(existingUser: User) = User(
        username = username ?: existingUser.username,
        email = email ?: existingUser.email,
        password = password ?: existingUser.password
    )
}