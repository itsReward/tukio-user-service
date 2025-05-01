package com.tukio.userservice.service

import com.tukio.userservice.dto.*
import com.tukio.userservice.exception.ResourceNotFoundException
import com.tukio.userservice.exception.UsernameAlreadyExistsException
import com.tukio.userservice.model.Role
import com.tukio.userservice.model.User
import com.tukio.userservice.repository.RoleRepository
import com.tukio.userservice.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found with username: $username") }

        val authorities = user.roles.map { role ->
            SimpleGrantedAuthority("ROLE_${role.name}")
        }

        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            user.enabled,
            user.accountNonExpired,
            user.credentialsNonExpired,
            user.accountNonLocked,
            authorities
        )
    }

    override fun getAllUsers(): List<UserDTO> {
        return userRepository.findAll().map { it.toDTO() }
    }

    override fun getUserById(id: Long): UserDTO {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        return user.toDTO()
    }

    override fun getUserByUsername(username: String): UserDTO {
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not found with username: $username") }
        return user.toDTO()
    }

    @Transactional
    override fun registerUser(request: UserRegistrationRequest): UserDTO {
        // Check if username already exists
        if (userRepository.existsByUsername(request.username)) {
            throw UsernameAlreadyExistsException("Username already exists: ${request.username}")
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email)) {
            throw UsernameAlreadyExistsException("Email already in use: ${request.email}")
        }

        // Get the default USER role
        val userRole = roleRepository.findByName("USER")
            .orElseThrow { ResourceNotFoundException("Default role not found") }

        // Create the new user
        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            department = request.department,
            studentId = request.studentId,
            graduationYear = request.graduationYear,
            interests = request.interests?.toMutableSet() ?: mutableSetOf(),
            roles = mutableSetOf(userRole)
        )

        val savedUser = userRepository.save(user)
        logger.info("Created new user: ${savedUser.id} - ${savedUser.username}")

        return savedUser.toDTO()
    }

    @Transactional
    override fun updateUser(id: Long, request: UserUpdateRequest): UserDTO {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }

        // Update fields if provided
        request.firstName?.let { user.firstName = it }
        request.lastName?.let { user.lastName = it }
        request.profilePictureUrl?.let { user.profilePictureUrl = it }
        request.bio?.let { user.bio = it }
        request.department?.let { user.department = it }
        request.graduationYear?.let { user.graduationYear = it }

        request.interests?.let {
            user.interests.clear()
            user.interests.addAll(it)
        }

        user.updatedAt = LocalDateTime.now()
        val updatedUser = userRepository.save(user)
        logger.info("Updated user: ${updatedUser.id} - ${updatedUser.username}")

        return updatedUser.toDTO()
    }

    @Transactional
    override fun changePassword(id: Long, request: PasswordChangeRequest): Boolean {
        // Validate that new password and confirm password match
        if (request.newPassword != request.confirmPassword) {
            throw IllegalArgumentException("New password and confirm password do not match")
        }

        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        // Update password
        user.password = passwordEncoder.encode(request.newPassword)
        user.updatedAt = LocalDateTime.now()
        userRepository.save(user)
        logger.info("Password changed for user: ${user.id} - ${user.username}")

        return true
    }

    @Transactional
    override fun addRoleToUser(userId: Long, roleName: String): UserDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val role = roleRepository.findByName(roleName)
            .orElseThrow { ResourceNotFoundException("Role not found with name: $roleName") }

        if (user.roles.any { it.name == roleName }) {
            // User already has this role
            return user.toDTO()
        }

        user.roles.add(role)
        user.updatedAt = LocalDateTime.now()

        val updatedUser = userRepository.save(user)
        logger.info("Added role $roleName to user: ${user.id} - ${user.username}")

        return updatedUser.toDTO()
    }

    @Transactional
    override fun removeRoleFromUser(userId: Long, roleName: String): UserDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        // Find the role to remove
        val roleToRemove = user.roles.find { it.name == roleName }
            ?: return user.toDTO() // Role not found, no action needed

        // Make sure user has at least one role remaining
        if (user.roles.size <= 1) {
            throw IllegalStateException("Cannot remove the only role from user")
        }

        user.roles.remove(roleToRemove)
        user.updatedAt = LocalDateTime.now()

        val updatedUser = userRepository.save(user)
        logger.info("Removed role $roleName from user: ${user.id} - ${user.username}")

        return updatedUser.toDTO()
    }

    @Transactional
    override fun updateUserInterests(userId: Long, interests: Set<String>): UserDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        user.interests.clear()
        user.interests.addAll(interests)
        user.updatedAt = LocalDateTime.now()

        val updatedUser = userRepository.save(user)
        logger.info("Updated interests for user: ${user.id} - ${user.username}")

        return updatedUser.toDTO()
    }

    @Transactional
    override fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User not found with id: $id")
        }

        userRepository.deleteById(id)
        logger.info("Deleted user with id: $id")
    }

    override fun searchUsers(keyword: String): List<UserDTO> {
        return userRepository.searchUsers(keyword).map { it.toDTO() }
    }

    override fun getUsersByInterests(interests: Set<String>): List<UserDTO> {
        // Require at least one matching interest
        val minMatchCount = 1L
        return userRepository.findByInterests(interests, minMatchCount).map { it.toDTO() }
    }

    override fun getPublicUserProfile(userId: Long): UserPublicProfileDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        return UserPublicProfileDTO(
            id = user.id,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            profilePictureUrl = user.profilePictureUrl,
            bio = user.bio,
            department = user.department,
            graduationYear = user.graduationYear,
            interests = user.interests
        )
    }

    // Extension function to convert User entity to UserDTO
    private fun User.toDTO(): UserDTO {
        return UserDTO(
            id = this.id,
            username = this.username,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            profilePictureUrl = this.profilePictureUrl,
            bio = this.bio,
            department = this.department,
            studentId = this.studentId,
            graduationYear = this.graduationYear,
            roles = this.roles.map { it.name }.toSet(),
            interests = this.interests,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}