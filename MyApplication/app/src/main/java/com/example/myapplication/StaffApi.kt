package com.example.myapplication

import retrofit2.Call
import retrofit2.http.*

interface StaffApi {
    @GET("/api/staff/summary/{staffId}")
    fun getStaffSummary(@Path("staffId") staffId: Long): Call<StaffSummary>

    @GET("/api/beneficiaries")
    fun getAllBeneficiaries(): Call<List<Beneficiary>>

    @POST("/api/beneficiaries")
    fun addBeneficiary(
        @Body beneficiary: Beneficiary,
        @Query("projectId") projectId: Long,
        @Query("addedById") addedById: Long
    ): Call<Beneficiary>

    @PUT("/api/beneficiaries/{id}")
    fun updateBeneficiary(
        @Path("id") id: Long,
        @Body beneficiary: Beneficiary,
        @Query("projectId") projectId: Long?,
        @Query("addedById") addedById: Long?
    ): Call<Beneficiary>

    @DELETE("/api/beneficiaries/{id}")
    fun deleteBeneficiary(@Path("id") id: Long): Call<Void>

    @GET("/api/projects/assigned/{staffId}")
    fun getAssignedProjects(@Path("staffId") staffId: Long): Call<List<Project>>

    @GET("/api/allocations")
    fun getAllAllocations(): Call<List<Allocation>>

    @POST("/api/allocations")
    fun allocateDonation(
        @Query("projectId") projectId: Long,
        @Query("beneficiaryId") beneficiaryId: Long,
        @Query("amount") amount: Double,
        @Query("userId") userId: Long
    ): Call<Allocation>

    @GET("/api/beneficiaries/project/{projectId}")
    fun getBeneficiariesByProject(@Path("projectId") projectId: Long): Call<List<Beneficiary>>

    @GET("/api/allocations/staff/{userId}")
    fun getAllocationsByStaff(@Path("userId") userId: Long): Call<List<Allocation>>
} 