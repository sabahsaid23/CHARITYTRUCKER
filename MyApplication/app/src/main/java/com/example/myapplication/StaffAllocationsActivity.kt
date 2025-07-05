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

class StaffAllocationsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private var staffId: Long = 1L
    private val BASE_URL = "http://10.78.83.106:8080"
    private var allocations: List<Allocation> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_allocations)
        staffId = intent.getLongExtra("staff_id", 1L)
        recyclerView = findViewById(R.id.allocationsRecyclerView)
        emptyText = findViewById(R.id.emptyText)
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchAllocations()
    }

    override fun onResume() {
        super.onResume()
        // Refresh allocations when returning to this activity
        fetchAllocations()
    }

    private fun fetchAllocations() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(StaffApi::class.java)
        api.getAllocationsByStaff(staffId).enqueue(object : Callback<List<Allocation>> {
            override fun onResponse(call: Call<List<Allocation>>, response: Response<List<Allocation>>) {
                if (response.isSuccessful && response.body() != null) {
                    allocations = response.body()!!
                    updateUI()
                } else {
                    Toast.makeText(this@StaffAllocationsActivity, "Failed to load allocations", Toast.LENGTH_SHORT).show()
                    showEmptyState("Failed to load allocations")
                }
            }
            override fun onFailure(call: Call<List<Allocation>>, t: Throwable) {
                Toast.makeText(this@StaffAllocationsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                showEmptyState("Network error occurred")
            }
        })
    }

    private fun updateUI() {
        if (allocations.isEmpty()) {
            showEmptyState("No allocations found")
        } else {
            recyclerView.adapter = AllocationAdapter(allocations)
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        }
    }

    private fun showEmptyState(message: String) {
        recyclerView.visibility = View.GONE
        emptyText.visibility = View.VISIBLE
        emptyText.text = message
    }
} 