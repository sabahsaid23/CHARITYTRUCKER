package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Query

interface AdminApi {
    @GET("/api/admin/summary")
    fun getSummary(): Call<AdminSummary>

    @GET("/api/donations")
    fun getAllDonations(): Call<List<Donation>>

    @POST("/api/donations/approve/{id}")
    fun approveDonation(@Path("id") id: Long): Call<Donation>

    @POST("/api/donations/reject/{id}")
    fun rejectDonation(@Path("id") id: Long): Call<Donation>

    @GET("/api/projects")
    fun getAllProjects(): Call<List<Project>>

    @POST("/api/projects")
    fun createProject(@Body project: Project, @Query("assignedStaffId") assignedStaffId: Long?): Call<Project>

    @PUT("/api/projects/{id}")
    fun updateProject(@Path("id") id: Long, @Body project: Project, @Query("assignedStaffId") assignedStaffId: Long?): Call<Project>

    @DELETE("/api/projects/{id}")
    fun deleteProject(@Path("id") id: Long): Call<Void>

    @GET("/api/users")
    fun getAllUsers(): Call<List<User>>

    @PUT("/api/users/{id}")
    fun updateUser(@Path("id") id: Long, @Body request: RegisterRequest): Call<User>

    @DELETE("/api/users/{id}")
    fun deleteUser(@Path("id") id: Long): Call<Void>

    @POST("/api/users/register-staff")
    fun registerStaff(@Body request: RegisterRequest): Call<User>

    @GET("/api/allocations")
    fun getAllAllocations(): Call<List<Allocation>>

    @POST("/api/allocations")
    fun addAllocation(
        @Query("projectId") projectId: Long,
        @Query("beneficiaryId") beneficiaryId: Long,
        @Query("amount") amount: Double,
        @Query("userId") userId: Long
    ): Call<Allocation>

    @GET("/api/beneficiaries/project/{projectId}")
    fun getBeneficiariesByProject(@Path("projectId") projectId: Long): Call<List<Beneficiary>>

    @GET("/api/projects/assigned/{staffId}")
    fun getProjectsByAssignedStaff(@Path("staffId") staffId: Long): Call<List<Project>>
} 