package com.coway.user.data.dto

import com.coway.user.data.entity.User

data class UserResponse(
    val username: String,
    val email: String,
    val token: String,
) {
    companion object {
        @JvmStatic
        fun build(user: User, token: String): UserResponse = UserResponse(
            username = user.username,
            email = user.email,
            token = token,
        )
    }
}