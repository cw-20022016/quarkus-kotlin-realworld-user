package com.coway.user.data.dto

import com.coway.user.data.entity.User
import com.coway.util.Alphanumerical

//@JsonRootName("user")
//@RegisterForReflection
data class UserRegistrationRequest(
    @field:Alphanumerical
    val username: String,
    val email: String,
    val password: String,
) {
    fun toEntity() = User(
        username = username,
        email = email,
        password = password
    )
}