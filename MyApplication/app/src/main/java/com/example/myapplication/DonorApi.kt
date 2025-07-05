package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DonorApi {
    @GET("/api/donations/summary/{donorId}")
    fun getDonorSummary(@Path("donorId") donorId: Long): Call<DonorSummary>
} 