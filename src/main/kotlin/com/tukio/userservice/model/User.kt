package com.tukio.userservice.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    @JsonIgnore
    var password: String,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = true)
    var profilePictureUrl: String? = null,

    @Column(nullable = true, length = 1000)
    var bio: String? = null,

    @Column(nullable = true)
    var department: String? = null,

    @Column(nullable = true)
    var studentId: String? = null,

    @Column(nullable = true)
    var graduationYear: Int? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf(),

    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "interest")
    var interests: MutableSet<String> = mutableSetOf(),

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(nullable = false)
    var accountNonExpired: Boolean = true,

    @Column(nullable = false)
    var accountNonLocked: Boolean = true,

    @Column(nullable = false)
    var credentialsNonExpired: Boolean = true,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)