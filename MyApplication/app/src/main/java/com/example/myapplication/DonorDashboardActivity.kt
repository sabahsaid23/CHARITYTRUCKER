package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DonorDashboardActivity : AppCompatActivity() {
    private lateinit var totalDonated: TextView
    private lateinit var numDonations: TextView
    private lateinit var makeDonationButton: Button
    private lateinit var myDonationsButton: Button
    private lateinit var projectsSupportedButton: Button
    private lateinit var userInfoText: TextView
    private var username: String? = null
    private var role: String? = null
    private val BASE_URL = "http://10.78.83.106:8080"
    private var donorId: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_dashboard)
        username = intent.getStringExtra("username")
        role = intent.getStringExtra("role")
        userInfoText = findViewById(R.id.userInfoText)
        userInfoText.text = "Logged in as: ${username ?: "Unknown"} (${role ?: "DONOR"})"

        totalDonated = findViewById(R.id.totalDonated)
        numDonations = findViewById(R.id.numDonations)
        makeDonationButton = findViewById(R.id.makeDonationButton)
        myDonationsButton = findViewById(R.id.myDonationsButton)
        projectsSupportedButton = findViewById(R.id.projectsSupportedButton)

        donorId = intent.getLongExtra("donor_id", 1L)

        makeDonationButton.setOnClickListener {
            val intent = Intent(this, MakeDonationActivity::class.java)
            intent.putExtra("donor_id", donorId)
            startActivity(intent)
        }
        myDonationsButton.setOnClickListener {
            val intent = Intent(this, MyDonationsActivity::class.java)
            intent.putExtra("donor_id", donorId)
            startActivity(intent)
        }
        projectsSupportedButton.setOnClickListener {
            val intent = Intent(this, ProjectsSupportedActivity::class.java)
            intent.putExtra("donor_id", donorId)
            startActivity(intent)
        }

        fetchDonorSummary()
    }

    override fun onResume() {
        super.onResume()
        fetchDonorSummary()
    }

    private fun fetchDonorSummary() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(DonorApi::class.java)
        api.getDonorSummary(donorId).enqueue(object : Callback<DonorSummary> {
            override fun onResponse(call: Call<DonorSummary>, response: Response<DonorSummary>) {
                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()!!
                    totalDonated.text = "Total Donated: $${summary.totalDonated ?: 0.0}"
                    numDonations.text = "Number of Donations: ${summary.numDonations ?: 0}"
                } else {
                    Toast.makeText(this@DonorDashboardActivity, "Failed to load summary", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<DonorSummary>, t: Throwable) {
                Toast.makeText(this@DonorDashboardActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 