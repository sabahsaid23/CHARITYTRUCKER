package com.example.myapplication

import java.time.LocalDateTime

data class LoginResponse(
    val id: Long?,
    val username: String?,
    val email: String?,
    val role: String?,
    val status: String?,
    val createdAt: String? // Use String for ISO date, can parse if needed
) 