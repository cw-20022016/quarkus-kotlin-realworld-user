package com.coway.user.resource

import com.coway.infra.web.Routes.USERS_PATH
import com.coway.infra.web.Routes.USER_PATH
import com.coway.infra.security.Role.ADMIN
import com.coway.infra.security.Role.USER
import com.coway.user.service.UserService
import com.coway.user.data.dto.UserLoginRequest
import com.coway.user.data.dto.UserRegistrationRequest
import com.coway.user.data.dto.UserUpdateRequest
import com.coway.util.ValidationMessage.Companion.REQUEST_BODY_MUST_NOT_BE_NULL
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.Response.Status.*
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.ok
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriBuilder.fromResource

@Path("/")
class UserResource(
    private val userService: UserService
) {
    @POST
    @Path(USERS_PATH)
    @Consumes(APPLICATION_JSON)
    @PermitAll
    fun register(
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) newUser: UserRegistrationRequest
    ): Response = userService.register(newUser).run {
        ok(this)
            .location(fromResource(UserResource::class.java).path("$USERS_PATH/$username").build())
            .status(CREATED).build()
    }

    @POST
    @Path("$USERS_PATH/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @PermitAll
    fun login(
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) userLoginRequest: UserLoginRequest
    ): Response = ok(userService.login(userLoginRequest)).status(OK).build()

    @GET
    @Path(USER_PATH)
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun getLoggedInUser(
        @Context securityContext: SecurityContext
    ): Response = ok(userService.get(securityContext.userPrincipal.name)).status(OK).build()

    @PUT
    @Path(USER_PATH)
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER, ADMIN)
    fun updateLoggedUser(
        @Context securityContext: SecurityContext,
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) userUpdateRequest: UserUpdateRequest,
    ): Response = ok(userService.update(securityContext.userPrincipal.name, userUpdateRequest)).status(OK).build()
}