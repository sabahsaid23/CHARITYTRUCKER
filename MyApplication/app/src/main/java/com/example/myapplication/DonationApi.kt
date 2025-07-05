package com.example.myapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DonationApi {
    @GET("/api/projects")
    fun getProjects(): Call<List<Project>>

    @POST("/api/donations")
    fun makeDonation(@Body donationRequest: DonationRequest): Call<Void>

    @GET("/api/donations/donor/{donorId}")
    fun getDonationsByDonor(@Path("donorId") donorId: Long): Call<List<Donation>>

    @GET("/api/donations/supported-projects/{donorId}")
    fun getSupportedProjects(@Path("donorId") donorId: Long): Call<List<Project>>
} 