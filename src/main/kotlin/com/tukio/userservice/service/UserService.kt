package com.tukio.userservice.service

import com.tukio.userservice.dto.*
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

interface UserService : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): org.springframework.security.core.userdetails.UserDetails

    fun getAllUsers(): List<UserDTO>

    fun getUserById(id: Long): UserDTO

    fun getUserByUsername(username: String): UserDTO

    fun registerUser(request: UserRegistrationRequest): UserDTO

    fun updateUser(id: Long, request: UserUpdateRequest): UserDTO

    fun changePassword(id: Long, request: PasswordChangeRequest): Boolean

    fun addRoleToUser(userId: Long, roleName: String): UserDTO

    fun removeRoleFromUser(userId: Long, roleName: String): UserDTO

    fun updateUserInterests(userId: Long, interests: Set<String>): UserDTO

    fun deleteUser(id: Long)

    fun searchUsers(keyword: String): List<UserDTO>

    fun getUsersByInterests(interests: Set<String>): List<UserDTO>

    fun getPublicUserProfile(userId: Long): UserPublicProfileDTO
}