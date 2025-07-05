package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyDonationsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: DonationAdapter
    private val donations = mutableListOf<Donation>()
    private val BASE_URL = "http://10.78.83.106:8080"
    private var donorId: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_donations)
        donorId = intent.getLongExtra("donor_id", 1L)

        recyclerView = findViewById(R.id.donationsRecyclerView)
        emptyText = findViewById(R.id.emptyText)
        adapter = DonationAdapter(donations) { }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchMyDonations()
    }

    private fun fetchMyDonations() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(DonationApi::class.java)
        api.getDonationsByDonor(donorId).enqueue(object : Callback<List<Donation>> {
            override fun onResponse(call: Call<List<Donation>>, response: Response<List<Donation>>) {
                if (response.isSuccessful && response.body() != null) {
                    donations.clear()
                    donations.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    emptyText.visibility = if (donations.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this@MyDonationsActivity, "Failed to load donations", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Donation>>, t: Throwable) {
                Toast.makeText(this@MyDonationsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 