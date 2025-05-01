package com.tukio.userservice.service

import com.tukio.userservice.dto.LoginRequest
import com.tukio.userservice.dto.LoginResponse
import com.tukio.userservice.dto.UserDTO
import com.tukio.userservice.dto.UserRegistrationRequest

interface AuthService {
    fun login(loginRequest: LoginRequest): LoginResponse
    fun register(registrationRequest: UserRegistrationRequest): UserDTO
    fun validateToken(token: String): Boolean
}