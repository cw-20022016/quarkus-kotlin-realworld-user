package com.coway.user

import com.coway.infra.security.Role.USER
import com.coway.infra.web.Routes.USERS_PATH
import com.coway.user.data.dto.UserLoginRequest
import com.coway.user.data.dto.UserRegistrationRequest
import com.coway.user.data.dto.UserResponse
import com.coway.user.data.dto.UserUpdateRequest
import com.coway.user.resource.UserResource
import com.coway.user.service.UserService
import com.coway.user.support.UserFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.quarkus.test.security.TestSecurity
import jakarta.inject.Inject
import jakarta.ws.rs.core.HttpHeaders.LOCATION
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.Response.Status.CREATED
import jakarta.ws.rs.core.Response.Status.BAD_REQUEST
import jakarta.ws.rs.core.Response.Status.OK
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*

@QuarkusTest
@TestHTTPEndpoint(UserResource::class)
internal class UserResourceIT {
    @InjectMock
    lateinit var userService: UserService
    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun register_WhenNewUser_ThenReturnCreatedResponse() {
        val token = "GENERATED_TOKEN"
        val newUser = UserFactory.create()
        val userRegistrationReq = newUser.run {
            UserRegistrationRequest(username, email, password)
        }
        `when`(userService.register(userRegistrationReq)).thenReturn(
            UserResponse.build(newUser, token)
        )

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(userRegistrationReq))
            .`when`()
            .post("/users")
            .then()
            .header(LOCATION, containsString("$USERS_PATH/${newUser.username}"))
            .statusCode(CREATED.statusCode)

        verify(userService).register(userRegistrationReq)
    }

    @Test
    fun register_WhenInvalidNewUser_ThenReturnBadRequest() {
        val invalidEntity = UserFactory.create(username = "$&^%@#!", email = "@@@@")

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(invalidEntity)
            .`when`()
            .post("/users")
            .then()
            .statusCode(BAD_REQUEST.statusCode)
    }

    @Test
    fun login_WhenValidRequest_ThenReturnOK() {
        val token = "GENERATED_TOKEN"
        val requestedUser = UserFactory.create().run {
            UserResponse(username, email, token)
        }
        val userLoginRequest = UserLoginRequest(requestedUser.email, token)

        `when`(userService.login(userLoginRequest)).thenReturn(requestedUser)

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(userLoginRequest))
            .`when`()
            .post("/users/login")
            .then()
            .body("size()", equalTo(3))
            .body("username", equalTo(requestedUser.username))
            .body("email", equalTo(requestedUser.email))
            .body("password", nullValue())
            .body("token", notNullValue())
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(userService).login(userLoginRequest)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun login_WhenAlreadyLoggedInUser_ThenReturnOK() {
        val token = "GENERATED_TOKEN"
        val loggedInUser = UserFactory.create(username = "loggedInUser").run {
            UserResponse(username, email, token)
        }

        `when`(userService.get(loggedInUser.username)).thenReturn(loggedInUser)

        given()
            .accept(APPLICATION_JSON)
            .get("/user")
            .then()
            .body("size()", equalTo(3))
            .body("username", equalTo(loggedInUser.username))
            .body("email", equalTo(loggedInUser.email))
            .body("password", nullValue())
            .body("token", notNullValue())
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(userService).get(loggedInUser.username)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun update_WhenValidRequest_ThenReturnOK() {
        val token = "GENERATED_TOKEN"
        val loggedInUser = UserFactory.create(username = "loggedInUser")
        val userUpdateRequest = loggedInUser.run {
            UserUpdateRequest("newUsername", null, null)
        }

        `when`(userService.update(loggedInUser.username, userUpdateRequest))
            .thenReturn(
                UserResponse(
                    username = userUpdateRequest.username!!,
                    email = loggedInUser.email,
                    token = token
                )
            )

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(userUpdateRequest))
            .`when`()
            .put("/user")
            .then()
            .body("size()", equalTo(3))
            .body("username", equalTo(userUpdateRequest.username))
            .body("email", equalTo(loggedInUser.email))
            .body("password", nullValue())
            .body("token", notNullValue())
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(userService).update(loggedInUser.username, userUpdateRequest)
    }
}