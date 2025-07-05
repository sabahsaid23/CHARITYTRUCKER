package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MakeDonationActivity : AppCompatActivity() {
    private lateinit var projectSpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var donateButton: Button
    private lateinit var progressBar: ProgressBar
    private var projectList: List<Project> = listOf()
    private val BASE_URL = "http://10.78.83.106:8080"
    private var donorId: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_donation)
        donorId = intent.getLongExtra("donor_id", 1L)

        projectSpinner = findViewById(R.id.projectSpinner)
        amountEditText = findViewById(R.id.amountEditText)
        donateButton = findViewById(R.id.donateButton)
        progressBar = ProgressBar(this)
        progressBar.visibility = View.GONE

        fetchProjects()

        donateButton.setOnClickListener {
            makeDonation()
        }
    }

    private fun fetchProjects() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(DonationApi::class.java)
        api.getProjects().enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful && response.body() != null) {
                    projectList = response.body()!!
                    val projectNames = projectList.map { it.name }
                    val adapter = ArrayAdapter(this@MakeDonationActivity, android.R.layout.simple_spinner_item, projectNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    projectSpinner.adapter = adapter
                } else {
                    Toast.makeText(this@MakeDonationActivity, "Failed to load projects", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Toast.makeText(this@MakeDonationActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun makeDonation() {
        val amountText = amountEditText.text.toString()
        if (amountText.isBlank()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        if (projectList.isEmpty()) {
            Toast.makeText(this, "No project selected", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedIndex = projectSpinner.selectedItemPosition
        val selectedProject = projectList[selectedIndex]
        val donationRequest = DonationRequest(donorId, selectedProject.id!!, amount)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(DonationApi::class.java)
        donateButton.isEnabled = false
        api.makeDonation(donationRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                donateButton.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@MakeDonationActivity, "Donation successful!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@MakeDonationActivity, "Donation failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                donateButton.isEnabled = true
                Toast.makeText(this@MakeDonationActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 