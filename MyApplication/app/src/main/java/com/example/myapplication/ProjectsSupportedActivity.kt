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

class ProjectsSupportedActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: ProjectAdapter
    private val projects = mutableListOf<Project>()
    private val BASE_URL = "http://10.78.83.106:8080"
    private var donorId: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects_supported)
        donorId = intent.getLongExtra("donor_id", 1L)

        recyclerView = findViewById(R.id.projectsRecyclerView)
        emptyText = findViewById(R.id.emptyText)
        adapter = ProjectAdapter(projects) { }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchSupportedProjects()
    }

    private fun fetchSupportedProjects() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(DonationApi::class.java)
        api.getSupportedProjects(donorId).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful && response.body() != null) {
                    projects.clear()
                    projects.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    emptyText.visibility = if (projects.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this@ProjectsSupportedActivity, "Failed to load projects", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Toast.makeText(this@ProjectsSupportedActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 