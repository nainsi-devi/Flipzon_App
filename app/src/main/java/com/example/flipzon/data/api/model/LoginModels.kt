package com.example.flipzon.data.api.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String,
    val token: String
)
