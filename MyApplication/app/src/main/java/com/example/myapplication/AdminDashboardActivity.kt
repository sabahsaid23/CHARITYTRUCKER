package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var totalUsers: TextView
    private lateinit var totalDonations: TextView
    private lateinit var totalProjects: TextView
    private lateinit var activeProjects: TextView
    private lateinit var inactiveProjects: TextView
    private lateinit var totalBeneficiaries: TextView
    private lateinit var errorMsg: TextView
    private lateinit var userInfoText: TextView
    private var username: String? = null
    private var role: String? = null

    private val BASE_URL = "http://10.78.83.106:8080" // Change if needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        username = intent.getStringExtra("username")
        role = intent.getStringExtra("role")
        userInfoText = findViewById(R.id.userInfoText)
        userInfoText.text = "Logged in as: ${username ?: "Unknown"} (${role ?: "ADMIN"})"

        totalUsers = findViewById(R.id.totalUsers)
        totalDonations = findViewById(R.id.totalDonations)
        totalProjects = findViewById(R.id.totalProjects)
        activeProjects = findViewById(R.id.activeProjects)
        inactiveProjects = findViewById(R.id.inactiveProjects)
        totalBeneficiaries = findViewById(R.id.totalBeneficiaries)
        errorMsg = findViewById(R.id.errorMsg)

        fetchSummary()

        val viewDonationsButton: android.widget.Button = findViewById(R.id.viewDonationsButton)
        viewDonationsButton.setOnClickListener {
            val intent = Intent(this, AdminDonationsActivity::class.java)
            startActivity(intent)
        }

        val viewProjectsButton: android.widget.Button = findViewById(R.id.viewProjectsButton)
        viewProjectsButton.setOnClickListener {
            val intent = Intent(this, AdminProjectsActivity::class.java)
            startActivity(intent)
        }

        val viewUsersButton: android.widget.Button = findViewById(R.id.viewUsersButton)
        viewUsersButton.setOnClickListener {
            val intent = Intent(this, AdminUsersActivity::class.java)
            startActivity(intent)
        }

        val viewAllocationsButton: android.widget.Button = findViewById(R.id.viewAllocationsButton)
        viewAllocationsButton.setOnClickListener {
            val intent = Intent(this, AdminAllocationsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchSummary()
    }

    private fun fetchSummary() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AdminApi::class.java)
        api.getSummary().enqueue(object : Callback<AdminSummary> {
            override fun onResponse(call: Call<AdminSummary>, response: Response<AdminSummary>) {
                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()!!
                    totalUsers.text = "Total Users: ${summary.totalUsers ?: "N/A"}"
                    totalDonations.text = "Total Donations: $${summary.totalDonations ?: "N/A"}"
                    totalProjects.text = "Total Projects: ${summary.totalProjects ?: "N/A"}"
                    activeProjects.text = "Active Projects: ${summary.activeProjects ?: "N/A"}"
                    inactiveProjects.text = "Inactive Projects: ${summary.inactiveProjects ?: "N/A"}"
                    totalBeneficiaries.text = "Total Beneficiaries: ${summary.totalBeneficiaries ?: "N/A"}"
                    errorMsg.visibility = View.GONE
                } else {
                    showError("Failed to load summary.")
                }
            }
            override fun onFailure(call: Call<AdminSummary>, t: Throwable) {
                showError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun showError(message: String) {
        errorMsg.text = message
        errorMsg.visibility = View.VISIBLE
    }
}