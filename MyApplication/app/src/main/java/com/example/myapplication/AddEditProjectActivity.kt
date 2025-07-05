package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.view.View
import android.widget.TextView

class AddEditProjectActivity : AppCompatActivity() {
    private lateinit var staffSpinner: Spinner
    private var staffList: List<User> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_project)

        val nameInput: EditText = findViewById(R.id.projectNameInput)
        val descInput: EditText = findViewById(R.id.projectDescInput)
        val targetInput: EditText = findViewById(R.id.projectTargetInput)
        val statusSpinner: Spinner = findViewById(R.id.projectStatusSpinner)
        staffSpinner = findViewById(R.id.projectStaffSpinner)
        val saveButton: Button = findViewById(R.id.saveProjectButton)
        val deleteButton: Button = findViewById(R.id.deleteProjectButton)

        val staffMode = intent.getBooleanExtra("staff_mode", false)
        val staffIdFromIntent = intent.getLongExtra("staff_id", -1L)
        if (staffMode) {
            staffSpinner.visibility = View.GONE
        }

        // Setup status spinner
        val statuses = arrayOf("ACTIVE", "INACTIVE")
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        // Check if editing an existing project
        val projectId = intent.getLongExtra("project_id", -1L)
        val isEdit = projectId != -1L
        var preselectStaffId: Long? = null
        if (isEdit) {
            nameInput.setText(intent.getStringExtra("project_name") ?: "")
            descInput.setText(intent.getStringExtra("project_desc") ?: "")
            targetInput.setText(intent.getDoubleExtra("project_target", 0.0).toString())
            val status = intent.getStringExtra("project_status") ?: "ACTIVE"
            val statusIndex = statuses.indexOf(status)
            if (statusIndex >= 0) statusSpinner.setSelection(statusIndex)
            preselectStaffId = intent.getLongExtra("assigned_staff_id", -1L).takeIf { it != -1L }
            saveButton.text = "Update Project"
            deleteButton.visibility = View.VISIBLE
        } else {
            deleteButton.visibility = View.GONE
        }

        if (!staffMode) {
            fetchStaffList(preselectStaffId)
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val desc = descInput.text.toString().trim()
            val target = targetInput.text.toString().trim().toDoubleOrNull()
            val status = statusSpinner.selectedItem.toString()
            val assignedStaffId = if (staffMode && staffIdFromIntent != -1L) staffIdFromIntent else {
                val staffIndex = staffSpinner.selectedItemPosition
                if (staffIndex >= 0 && staffList.isNotEmpty()) staffList[staffIndex].id else null
            }

            if (name.isEmpty() || desc.isEmpty() || target == null || assignedStaffId == null) {
                Toast.makeText(this, "Please fill all fields and select staff", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val project = Project(
                id = if (isEdit) projectId else null,
                name = name,
                description = desc,
                targetAmount = target,
                receivedAmount = 0.0,
                status = status,
                createdAt = null,
                assignedStaffId = assignedStaffId
            )

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.78.83.106:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(AdminApi::class.java)
            if (isEdit) {
                api.updateProject(projectId, project, assignedStaffId).enqueue(object : Callback<Project> {
                    override fun onResponse(call: Call<Project>, response: Response<Project>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddEditProjectActivity, "Project updated!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddEditProjectActivity, "Failed to update project", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Project>, t: Throwable) {
                        Toast.makeText(this@AddEditProjectActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                api.createProject(project, assignedStaffId).enqueue(object : Callback<Project> {
                    override fun onResponse(call: Call<Project>, response: Response<Project>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddEditProjectActivity, "Project created!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddEditProjectActivity, "Failed to create project", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Project>, t: Throwable) {
                        Toast.makeText(this@AddEditProjectActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        deleteButton.setOnClickListener {
            if (isEdit) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.78.83.106:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(AdminApi::class.java)
                api.deleteProject(projectId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddEditProjectActivity, "Project deleted!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddEditProjectActivity, "Failed to delete project", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@AddEditProjectActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun fetchStaffList(preselectStaffId: Long?) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.78.83.106:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AdminApi::class.java)
        api.getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    staffList = response.body()!!.filter { it.role == "STAFF" }
                    val staffNames = staffList.map { it.username ?: "Unknown" }
                    staffSpinner.adapter = ArrayAdapter(this@AddEditProjectActivity, android.R.layout.simple_spinner_item, staffNames)
                    // Preselect staff if editing
                    if (preselectStaffId != null) {
                        val staffIndex = staffList.indexOfFirst { it.id == preselectStaffId }
                        if (staffIndex >= 0) staffSpinner.setSelection(staffIndex)
                    }
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@AddEditProjectActivity, "Failed to load staff", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 