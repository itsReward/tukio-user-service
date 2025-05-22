package com.tukio.userservice.controller

import com.tukio.userservice.dto.PasswordChangeRequest
import com.tukio.userservice.dto.UserDTO
import com.tukio.userservice.dto.UserPublicProfileDTO
import com.tukio.userservice.dto.UserUpdateRequest
import com.tukio.userservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(): ResponseEntity<List<UserDTO>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.getUserById(id))
    }

    @GetMapping("/me")
    fun getCurrentUser(@RequestParam username: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.getUserByUsername(username))
    }

    @GetMapping("/profile/{id}")
    fun getUserProfile(@PathVariable id: Long): ResponseEntity<UserPublicProfileDTO> {
        return ResponseEntity.ok(userService.getPublicUserProfile(id))
    }

    @GetMapping("/search")
    fun searchUsers(@RequestParam keyword: String): ResponseEntity<List<UserDTO>> {
        return ResponseEntity.ok(userService.searchUsers(keyword))
    }

    @GetMapping("/interests")
    fun getUsersByInterests(@RequestParam interests: Set<String>): ResponseEntity<List<UserDTO>> {
        return ResponseEntity.ok(userService.getUsersByInterests(interests))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody updateRequest: UserUpdateRequest
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.updateUser(id, updateRequest))
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("@userSecurity.isCurrentUser(#id)")
    fun changePassword(
        @PathVariable id: Long,
        @RequestBody passwordChangeRequest: PasswordChangeRequest
    ): ResponseEntity<Map<String, Boolean>> {
        val success = userService.changePassword(id, passwordChangeRequest)
        return ResponseEntity.ok(mapOf("success" to success))
    }

    @PutMapping("/{id}/interests")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    fun updateUserInterests(
        @PathVariable id: Long,
        @RequestBody interests: Set<String>
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.updateUserInterests(id, interests))
    }

    @PutMapping("/{userId}/roles/add/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun addRoleToUser(
        @PathVariable userId: Long,
        @PathVariable roleName: String
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.addRoleToUser(userId, roleName))
    }

    @PutMapping("/{userId}/roles/remove/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeRoleFromUser(
        @PathVariable userId: Long,
        @PathVariable roleName: String
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.removeRoleFromUser(userId, roleName))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}