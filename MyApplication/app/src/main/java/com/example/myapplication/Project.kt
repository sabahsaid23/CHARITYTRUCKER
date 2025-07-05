package com.example.myapplication

data class Project(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val targetAmount: Double? = null,
    val receivedAmount: Double? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val assignedStaffId: Long? = null
) 