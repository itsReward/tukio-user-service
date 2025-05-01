package com.tukio.userservice.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserSecurity {

    fun isCurrentUser(userId: Long): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        return authentication.authorities.any {
            it.authority == "ROLE_ADMIN"
        } || userId.toString() == username.substringAfterLast("_", "")
    }
}