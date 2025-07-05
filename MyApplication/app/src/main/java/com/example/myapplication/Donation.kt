package com.example.myapplication

data class Donation(
    val id: Long?,
    val amount: Double?,
    val donor: User?,
    val project: Project?,
    val date: String?,
    val status: String?
)

