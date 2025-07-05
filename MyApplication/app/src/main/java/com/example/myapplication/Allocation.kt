package com.example.myapplication

data class Allocation(
    val id: Long?,
    val project: Project?,
    val beneficiary: Beneficiary?,
    val amount: Double?,
    val date: String?,
    val staff: User?
) 