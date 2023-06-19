package com.coway.user.data.entity

import com.coway.util.Patterns
import com.coway.util.ValidationMessage.Companion.EMAIL_MUST_BE_NOT_BLANK
import com.coway.util.ValidationMessage.Companion.PASSWORD_MUST_BE_NOT_BLANK
import com.coway.util.ValidationMessage.Companion.USERNAME_MUST_MATCH_PATTERN
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Entity
@Table(name = "users")
@RegisterForReflection
class User (

    @Id
    @Column(unique = true)
    @field:Pattern(regexp = Patterns.ALPHANUMERICAL, message = USERNAME_MUST_MATCH_PATTERN)
//    @field:Alphanumerical
    var username: String = "",

    @field:Email
    @field:NotBlank(message = EMAIL_MUST_BE_NOT_BLANK)
    @Column(unique = true)
    var email: String = "",

    @field:NotBlank(message = PASSWORD_MUST_BE_NOT_BLANK)
    var password: String = "",

//    @Column(name = "bio")
//    var bio: String = "",
//
//    @Column(name = "image")
//    var image: String = "",
)