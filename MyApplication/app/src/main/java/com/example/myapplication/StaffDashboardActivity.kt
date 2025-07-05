package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StaffDashboardActivity : AppCompatActivity() {
    private lateinit var totalBeneficiaries: TextView
    private lateinit var totalProjects: TextView
    private lateinit var totalAllocations: TextView
    private lateinit var manageBeneficiariesButton: Button
    private lateinit var assignedProjectsButton: Button
    private lateinit var allocateDonationsButton: Button
    private lateinit var userInfoText: TextView
    private lateinit var viewAllocationsButton: Button
    private val BASE_URL = "http://10.78.83.106:8080"
    private var staffId: Long = 1L // Default fallback
    private var username: String? = null
    private var role: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard)
        staffId = intent.getLongExtra("staff_id", 1L)
        username = intent.getStringExtra("username")
        role = intent.getStringExtra("role")
        userInfoText = findViewById(R.id.userInfoText)
        userInfoText.text = "Logged in as: ${username ?: "Unknown"} (ID: $staffId, ${role ?: "STAFF"})"

        totalBeneficiaries = findViewById(R.id.totalBeneficiaries)
        totalProjects = findViewById(R.id.totalProjects)
        totalAllocations = findViewById(R.id.totalAllocations)
        manageBeneficiariesButton = findViewById(R.id.manageBeneficiariesButton)
        assignedProjectsButton = findViewById(R.id.assignedProjectsButton)
        allocateDonationsButton = findViewById(R.id.allocateDonationsButton)
        viewAllocationsButton = findViewById(R.id.viewAllocationsButton)

        manageBeneficiariesButton.setOnClickListener {
            val intent = Intent(this, ManageBeneficiariesActivity::class.java)
            intent.putExtra("staff_id", staffId)
            startActivity(intent)
        }
        assignedProjectsButton.setOnClickListener {
            val intent = Intent(this, AssignedProjectsActivity::class.java)
            intent.putExtra("staff_id", staffId)
            startActivity(intent)
        }
        allocateDonationsButton.setOnClickListener {
            val intent = Intent(this, AllocateDonationsActivity::class.java)
            intent.putExtra("staff_id", staffId)
            startActivity(intent)
        }
        viewAllocationsButton.setOnClickListener {
            val intent = Intent(this, StaffAllocationsActivity::class.java)
            intent.putExtra("staff_id", staffId)
            startActivity(intent)
        }

        fetchStaffSummary()
    }

    override fun onResume() {
        super.onResume()
        fetchStaffSummary()
    }

    private fun fetchStaffSummary() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(StaffApi::class.java)
        api.getStaffSummary(staffId).enqueue(object : Callback<StaffSummary> {
            override fun onResponse(call: Call<StaffSummary>, response: Response<StaffSummary>) {
                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()!!
                    totalBeneficiaries.text = "Beneficiaries: ${summary.totalBeneficiaries ?: 0}"
                    totalProjects.text = "Projects: ${summary.totalProjects ?: 0}"
                    totalAllocations.text = "Allocations: ${summary.totalAllocations ?: 0}"
                } else {
                    Toast.makeText(this@StaffDashboardActivity, "Failed to load summary", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<StaffSummary>, t: Throwable) {
                Toast.makeText(this@StaffDashboardActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 