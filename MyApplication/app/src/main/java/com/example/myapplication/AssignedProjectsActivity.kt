package com.example.myapplication

import android.content.Intent
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

class AssignedProjectsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private val BASE_URL = "http://10.78.83.106:8080"
    private var projectList: List<Project> = listOf()
    private var staffId: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assigned_projects)
        recyclerView = findViewById(R.id.assignedProjectsRecyclerView)
        emptyText = findViewById(R.id.emptyText)
        staffId = intent.getLongExtra("staff_id", 1L)
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchAssignedProjects()
        val addBtn = findViewById<View>(R.id.btnAddProject)
        addBtn.visibility = View.GONE
    }

    private fun fetchAssignedProjects() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(StaffApi::class.java)
        api.getAssignedProjects(staffId).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful && response.body() != null) {
                    projectList = response.body()!!
                    if (projectList.isEmpty()) {
                        recyclerView.adapter = null
                        emptyText.visibility = TextView.VISIBLE
                    } else {
                        recyclerView.adapter = ProjectAdapter(projectList) { project ->
                            val intent = Intent(this@AssignedProjectsActivity, ProjectDetailsActivity::class.java)
                            intent.putExtra("project_id", project.id)
                            intent.putExtra("project_name", project.name)
                            intent.putExtra("project_desc", project.description)
                            intent.putExtra("project_target", project.targetAmount)
                            intent.putExtra("project_status", project.status)
                            intent.putExtra("project_received", project.receivedAmount)
                            intent.putExtra("project_created", project.createdAt)
                            startActivity(intent)
                        }
                        emptyText.visibility = TextView.GONE
                    }
                } else {
                    Toast.makeText(this@AssignedProjectsActivity, "Failed to load projects", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Toast.makeText(this@AssignedProjectsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 