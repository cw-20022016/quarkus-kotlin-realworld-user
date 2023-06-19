package com.coway.user.data.repository

import com.coway.user.data.entity.User
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class UserRepository: PanacheRepositoryBase<User, String> {

    fun findByEmail(email: String): User? =
        find("upper(email)", email.uppercase(Locale.getDefault()).trim())
            .firstResult()

    fun existsUsername(subjectedUsername: String): Boolean =
        count(query = "username = :subjectedUsername", params = Parameters.with("subjectedUsername", subjectedUsername)) > 0

    fun existsEmail(subjectedUserEmail: String): Boolean =
        count(query = "upper(email) = :subjectedUserEmail", params = Parameters.with("subjectedUserEmail", subjectedUserEmail.uppercase(Locale.getDefault()).trim())) > 0
}