package com.example.myapplication

data class DonationRequest(
    val donorId: Long,
    val projectId: Long,
    val amount: Double
) 