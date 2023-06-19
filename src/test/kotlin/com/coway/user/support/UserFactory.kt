package com.coway.user.support

import com.coway.user.data.entity.User
import java.util.UUID.randomUUID

class UserFactory {
    companion object {
        fun create(
            username: String = "User-${randomUUID()}".substring(0, 20),
            email: String = "$username@email.com",
            password: String = "password",
//            bio: String = "Hello, I am $username!",
//            image: String = "path/to/$username.jpg"
        ): User = User(username, email, password)
    }

    fun create(
        amount: Int,
    ): List<User> = (0 until amount).map { create() }
}