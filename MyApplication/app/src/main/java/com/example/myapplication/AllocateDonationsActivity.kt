package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

class AllocateDonationsActivity : AppCompatActivity() {
    private lateinit var projectSpinner: Spinner
    private lateinit var beneficiarySpinner: Spinner
    private lateinit var amountInput: EditText
    private lateinit var allocateButton: Button
    private lateinit var emptyText: TextView
    private var staffId: Long = 1L
    private var projectList: List<Project> = listOf()
    private var beneficiaryList: List<Beneficiary> = listOf()
    private val BASE_URL = "http://10.78.83.106:8080"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val api: StaffApi by lazy { retrofit.create(StaffApi::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allocate_donations)
        staffId = intent.getLongExtra("staff_id", 1L)
        projectSpinner = findViewById(R.id.projectSpinner)
        beneficiarySpinner = findViewById(R.id.beneficiarySpinner)
        amountInput = findViewById(R.id.amountInput)
        allocateButton = findViewById(R.id.allocateButton)
        emptyText = findViewById(R.id.emptyText)
        setupUI()
        intent.putExtra("staff_id", staffId)
    }

    private fun setupUI() {
        fetchProjects()
        allocateButton.setOnClickListener { allocateDonation() }
    }

    private fun fetchProjects() {
        Log.d("AllocateDonations", "Fetching assigned projects for staffId=$staffId")
        api.getAssignedProjects(staffId).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                Log.d("AllocateDonations", "Project fetch response: ${response.code()} body: ${response.body()} ")
                if (response.isSuccessful && response.body() != null) {
                    projectList = response.body()!!
                    Log.d("AllocateDonations", "Fetched ${projectList.size} projects: $projectList")
                    Toast.makeText(this@AllocateDonationsActivity, "Fetched ${projectList.size} projects", Toast.LENGTH_SHORT).show()
                    if (projectList.isEmpty()) {
                        showEmptyState("No assigned projects. Contact admin.")
                    } else {
                        val projectNames = projectList.map { it.name ?: "Unnamed" }
                        projectSpinner.adapter = ArrayAdapter(this@AllocateDonationsActivity, android.R.layout.simple_spinner_item, projectNames)
                        projectSpinner.setSelection(0)
                        projectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                Log.d("AllocateDonations", "Project selected: index=$position, name=${projectNames[position]}")
                                Toast.makeText(this@AllocateDonationsActivity, "Selected project: ${projectNames[position]}", Toast.LENGTH_SHORT).show()
                                fetchBeneficiariesForProject(projectList[position].id ?: -1L)
                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {
                                Log.d("AllocateDonations", "No project selected")
                            }
                        }
                        fetchBeneficiariesForProject(projectList[0].id ?: -1L)
                    }
                } else {
                    Log.d("AllocateDonations", "Failed to fetch projects: ${response.code()} ${response.message()}")
                    showEmptyState("Failed to load projects.")
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Log.d("AllocateDonations", "Network error fetching projects: ${t.localizedMessage}")
                showEmptyState("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun fetchBeneficiariesForProject(projectId: Long) {
        beneficiarySpinner.adapter = null
        api.getBeneficiariesByProject(projectId).enqueue(object : Callback<List<Beneficiary>> {
            override fun onResponse(call: Call<List<Beneficiary>>, response: Response<List<Beneficiary>>) {
                if (response.isSuccessful && response.body() != null) {
                    beneficiaryList = response.body()!!.filter { it.status == "ACTIVE" }
                    if (beneficiaryList.isEmpty()) {
                        allocateButton.isEnabled = false
                        Toast.makeText(this@AllocateDonationsActivity, "No beneficiaries for this project", Toast.LENGTH_SHORT).show()
                    } else {
                        val names = beneficiaryList.map { it.name ?: "Unnamed" }
                        beneficiarySpinner.adapter = ArrayAdapter(this@AllocateDonationsActivity, android.R.layout.simple_spinner_item, names)
                        allocateButton.isEnabled = true
                    }
                } else {
                    allocateButton.isEnabled = false
                    Toast.makeText(this@AllocateDonationsActivity, "Failed to load beneficiaries", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Beneficiary>>, t: Throwable) {
                allocateButton.isEnabled = false
                Toast.makeText(this@AllocateDonationsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun allocateDonation() {
        val projectIndex = projectSpinner.selectedItemPosition
        val beneficiaryIndex = beneficiarySpinner.selectedItemPosition
        if (projectIndex < 0 || beneficiaryIndex < 0 || projectList.isEmpty() || beneficiaryList.isEmpty()) {
            Toast.makeText(this, "Select project and beneficiary", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = amountInput.text.toString().toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        val projectId = projectList[projectIndex].id ?: return
        val beneficiaryId = beneficiaryList[beneficiaryIndex].id ?: return
        allocateButton.isEnabled = false
        api.allocateDonation(projectId, beneficiaryId, amount, staffId).enqueue(object : Callback<Allocation> {
            override fun onResponse(call: Call<Allocation>, response: Response<Allocation>) {
                allocateButton.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@AllocateDonationsActivity, "Allocation successful!", Toast.LENGTH_LONG).show()
                    amountInput.text.clear()
                } else {
                    Toast.makeText(this@AllocateDonationsActivity, "Allocation failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Allocation>, t: Throwable) {
                allocateButton.isEnabled = true
                Toast.makeText(this@AllocateDonationsActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEmptyState(message: String) {
        emptyText.visibility = View.VISIBLE
        emptyText.text = message
        projectSpinner.adapter = null
        beneficiarySpinner.adapter = null
        allocateButton.isEnabled = false
    }
} 