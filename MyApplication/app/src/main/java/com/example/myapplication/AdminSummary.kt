package com.example.myapplication

import java.math.BigDecimal

data class AdminSummary(
    val totalUsers: Long?,
    val totalDonations: Double?,
    val totalProjects: Long?,
    val activeProjects: Long?,
    val inactiveProjects: Long?,
    val totalBeneficiaries: Long?
) 