package com.example.myapplication

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

class AdminDonationDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_donation_detail)

        val donorName: TextView = findViewById(R.id.detailDonorName)
        val amount: TextView = findViewById(R.id.detailAmount)
        val projectName: TextView = findViewById(R.id.detailProjectName)
        val date: TextView = findViewById(R.id.detailDate)
        val approveButton: Button = findViewById(R.id.approveButton)
        val rejectButton: Button = findViewById(R.id.rejectButton)

        donorName.text = intent.getStringExtra("donor_name") ?: "Unknown Donor"
        amount.text = "Amount: $${intent.getDoubleExtra("amount", 0.0)}"
        projectName.text = intent.getStringExtra("project_name") ?: "Unknown Project"
        date.text = intent.getStringExtra("date") ?: "N/A"

        val donationId = intent.getLongExtra("donation_id", -1)
        val BASE_URL = "http://10.78.83.106:8080"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AdminApi::class.java)

        approveButton.setOnClickListener {
            if (donationId != -1L) {
                api.approveDonation(donationId).enqueue(object : Callback<Donation> {
                    override fun onResponse(call: Call<Donation>, response: Response<Donation>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminDonationDetailActivity, "Donation approved!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AdminDonationDetailActivity, "Failed to approve donation", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Donation>, t: Throwable) {
                        Toast.makeText(this@AdminDonationDetailActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        rejectButton.setOnClickListener {
            if (donationId != -1L) {
                api.rejectDonation(donationId).enqueue(object : Callback<Donation> {
                    override fun onResponse(call: Call<Donation>, response: Response<Donation>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminDonationDetailActivity, "Donation rejected!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AdminDonationDetailActivity, "Failed to reject donation", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Donation>, t: Throwable) {
                        Toast.makeText(this@AdminDonationDetailActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
} 