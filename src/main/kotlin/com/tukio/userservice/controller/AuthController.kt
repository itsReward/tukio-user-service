package com.tukio.userservice.controller

import com.tukio.userservice.dto.LoginRequest
import com.tukio.userservice.dto.LoginResponse
import com.tukio.userservice.dto.UserDTO
import com.tukio.userservice.dto.UserRegistrationRequest
import com.tukio.userservice.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResponse = authService.login(loginRequest)
        return ResponseEntity.ok(loginResponse)
    }

    @PostMapping("/register")
    fun register(@RequestBody registrationRequest: UserRegistrationRequest): ResponseEntity<UserDTO> {
        val user = authService.register(registrationRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @GetMapping("/validate")
    fun validateToken(@RequestParam token: String): ResponseEntity<Map<String, Boolean>> {
        val isValid = authService.validateToken(token)
        return ResponseEntity.ok(mapOf("valid" to isValid))
    }
}