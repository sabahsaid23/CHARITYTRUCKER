package com.example.myapplication

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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminProjectsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: ProjectAdapter
    private val projects = mutableListOf<Project>()
    private val allProjects = mutableListOf<Project>()
    private val BASE_URL = "http://10.78.83.106:8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_projects)

        recyclerView = findViewById(R.id.projectsRecyclerView)
        searchView = findViewById(R.id.searchView)
        adapter = ProjectAdapter(projects) { project ->
            val intent = android.content.Intent(this, AddEditProjectActivity::class.java)
            intent.putExtra("project_id", project.id)
            intent.putExtra("project_name", project.name)
            intent.putExtra("project_desc", project.description)
            intent.putExtra("project_target", project.targetAmount)
            intent.putExtra("project_status", project.status)
            // Add more fields as needed
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val addProjectFab: FloatingActionButton = findViewById(R.id.addProjectFab)
        addProjectFab.setOnClickListener {
            val intent = android.content.Intent(this, AddEditProjectActivity::class.java)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProjects(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterProjects(newText)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchProjects()
    }

    private fun fetchProjects() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AdminApi::class.java)
        api.getAllProjects().enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful && response.body() != null) {
                    allProjects.clear()
                    allProjects.addAll(response.body()!!)
                    projects.clear()
                    projects.addAll(allProjects)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AdminProjectsActivity, "Failed to load projects", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Toast.makeText(this@AdminProjectsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun filterProjects(query: String?) {
        val q = query?.trim()?.lowercase() ?: ""
        projects.clear()
        if (q.isEmpty()) {
            projects.addAll(allProjects)
        } else {
            projects.addAll(allProjects.filter {
                (it.name?.lowercase()?.contains(q) == true) ||
                (it.status?.lowercase()?.contains(q) == true) ||
                (it.targetAmount?.toString()?.contains(q) == true) ||
                (it.receivedAmount?.toString()?.contains(q) == true)
            })
        }
        adapter.notifyDataSetChanged()
    }
} 