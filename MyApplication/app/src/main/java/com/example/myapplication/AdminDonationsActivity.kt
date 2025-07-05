package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.appcompat.widget.SearchView

class AdminDonationsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonationAdapter
    private val donations = mutableListOf<Donation>()
    private val allDonations = mutableListOf<Donation>()
    private val BASE_URL = "http://10.78.83.106:8080"
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_donations)

        recyclerView = findViewById(R.id.donationsRecyclerView)
        searchView = findViewById(R.id.searchView)
        adapter = DonationAdapter(donations) { donation ->
            val intent = Intent(this, AdminDonationDetailActivity::class.java)
            intent.putExtra("donation_id", donation.id)
            intent.putExtra("donor_name", donation.donor?.username)
            intent.putExtra("amount", donation.amount)
            intent.putExtra("project_name", donation.project?.name)
            intent.putExtra("date", donation.date)
            // Add more fields as needed
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDonations(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterDonations(newText)
                return true
            }
        })

        fetchDonations()
    }

    private fun fetchDonations() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AdminApi::class.java)
        api.getAllDonations().enqueue(object : Callback<List<Donation>> {
            override fun onResponse(call: Call<List<Donation>>, response: Response<List<Donation>>) {
                if (response.isSuccessful && response.body() != null) {
                    allDonations.clear()
                    allDonations.addAll(response.body()!!)
                    donations.clear()
                    donations.addAll(allDonations)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AdminDonationsActivity, "Failed to load donations", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<List<Donation>>, t: Throwable) {
                Toast.makeText(this@AdminDonationsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun filterDonations(query: String?) {
        val q = query?.trim()?.lowercase() ?: ""
        donations.clear()
        if (q.isEmpty()) {
            donations.addAll(allDonations)
        } else {
            donations.addAll(allDonations.filter {
                (it.donor?.username?.lowercase()?.contains(q) == true) ||
                (it.project?.name?.lowercase()?.contains(q) == true) ||
                (it.amount?.toString()?.contains(q) == true)
            })
        }
        adapter.notifyDataSetChanged()
    }
} 