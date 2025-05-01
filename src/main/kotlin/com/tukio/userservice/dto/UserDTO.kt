package com.tukio.userservice.dto

import java.time.LocalDateTime

data class UserDTO(
    val id: Long?,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
    val bio: String?,
    val department: String?,
    val studentId: String?,
    val graduationYear: Int?,
    val roles: Set<String>,
    val interests: Set<String>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val department: String?,
    val studentId: String?,
    val graduationYear: Int?,
    val interests: Set<String>?
)

data class UserUpdateRequest(
    val firstName: String?,
    val lastName: String?,
    val profilePictureUrl: String?,
    val bio: String?,
    val department: String?,
    val graduationYear: Int?,
    val interests: Set<String>?
)

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val userId: Long,
    val username: String,
    val email: String,
    val roles: Set<String>,
    val expiresIn: Long
)

data class UserPublicProfileDTO(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
    val bio: String?,
    val department: String?,
    val graduationYear: Int?,
    val interests: Set<String>
)