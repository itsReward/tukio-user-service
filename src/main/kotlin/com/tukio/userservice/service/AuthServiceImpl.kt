package com.tukio.userservice.service

import com.tukio.userservice.dto.LoginRequest
import com.tukio.userservice.dto.LoginResponse
import com.tukio.userservice.dto.UserDTO
import com.tukio.userservice.dto.UserRegistrationRequest
import com.tukio.userservice.exception.InvalidCredentialsException
import com.tukio.userservice.repository.UserRepository
import com.tukio.userservice.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userService: UserService,
    private val userRepository: UserRepository
) : AuthService {

    private val logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)

    override fun login(loginRequest: LoginRequest): LoginResponse {
        try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )

            SecurityContextHolder.getContext().authentication = authentication
            val userDetails = authentication.principal as UserDetails
            val jwt = jwtService.generateToken(userDetails)

            val user = userRepository.findByUsername(userDetails.username).get()

            logger.info("User logged in: ${user.username}")

            return LoginResponse(
                token = jwt,
                userId = user.id,
                username = user.username,
                email = user.email,
                roles = user.roles.map { it.name }.toSet(),
                expiresIn = jwtService.getExpirationTime()
            )
        } catch (e: AuthenticationException) {
            logger.error("Authentication failed: ${e.message}")
            throw InvalidCredentialsException("Invalid username or password")
        }
    }

    override fun register(registrationRequest: UserRegistrationRequest): UserDTO {
        return userService.registerUser(registrationRequest)
    }

    override fun validateToken(token: String): Boolean {
        try {
            val username = jwtService.extractUsername(token)
            if (username != null) {
                val userDetails = userService.loadUserByUsername(username)
                return jwtService.isTokenValid(token, userDetails)
            }
            return false
        } catch (e: Exception) {
            logger.error("Token validation failed: ${e.message}")
            return false
        }
    }
}