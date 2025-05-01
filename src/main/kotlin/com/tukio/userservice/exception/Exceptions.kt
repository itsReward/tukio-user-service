package com.tukio.userservice.exception

import java.time.LocalDateTime

class ResourceNotFoundException(message: String) : RuntimeException(message)

class UsernameAlreadyExistsException(message: String) : RuntimeException(message)

class InvalidCredentialsException(message: String) : RuntimeException(message)

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)