package com.tukio.userservice.repository

import com.tukio.userservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>

    fun findByEmail(email: String): Optional<User>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    @Query("""
        SELECT u FROM User u
        JOIN u.interests i
        WHERE i IN :interests
        GROUP BY u.id
        HAVING COUNT(DISTINCT i) >= :minMatchCount
    """)
    fun findByInterests(
        @Param("interests") interests: Set<String>,
        @Param("minMatchCount") minMatchCount: Long
    ): List<User>

    @Query("""
        SELECT u FROM User u
        WHERE u.department = :department
    """)
    fun findByDepartment(@Param("department") department: String): List<User>

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    fun searchUsers(@Param("keyword") keyword: String): List<User>
}