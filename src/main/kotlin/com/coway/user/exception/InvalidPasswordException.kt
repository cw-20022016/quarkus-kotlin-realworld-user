package com.coway.user.exception

import io.quarkus.security.ForbiddenException

class InvalidPasswordException : ForbiddenException("Invalid password")