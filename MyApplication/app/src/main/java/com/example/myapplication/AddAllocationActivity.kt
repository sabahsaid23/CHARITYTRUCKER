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

class AddAllocationActivity : AppCompatActivity() {
    private lateinit var spinnerProject: Spinner
    private lateinit var spinnerBeneficiary: Spinner
    private lateinit var spinnerStaff: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    private var projects: List<Project> = listOf()
    private var beneficiaries: List<Beneficiary> = listOf()
    private var staff: List<User> = listOf()
    private var selectedProjectId: Long? = null
    private var selectedBeneficiaryId: Long? = null
    private var selectedStaffId: Long? = null

    private val BASE_URL = "http://10.78.83.106:8080"
    private lateinit var api: AdminApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_allocation)

        spinnerProject = findViewById(R.id.spinnerProject)
        spinnerBeneficiary = findViewById(R.id.spinnerBeneficiary)
        spinnerStaff = findViewById(R.id.spinnerStaff)
        editTextAmount = findViewById(R.id.editTextAmount)
        btnSubmit = findViewById(R.id.btnSubmitAllocation)
        progressBar = ProgressBar(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(AdminApi::class.java)

        fetchProjects()
        fetchStaff()

        spinnerProject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedProjectId = projects[position].id
                fetchBeneficiaries(selectedProjectId!!)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerBeneficiary.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedBeneficiaryId = beneficiaries[position].id
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerStaff.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStaffId = staff[position].id
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSubmit.setOnClickListener {
            submitAllocation()
        }
    }

    private fun fetchProjects() {
        api.getAllProjects().enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful && response.body() != null) {
                    projects = response.body()!!
                    val projectNames = projects.map { it.name ?: "Unnamed" }
                    val adapter = ArrayAdapter(this@AddAllocationActivity, android.R.layout.simple_spinner_item, projectNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerProject.adapter = adapter
                    if (projects.isNotEmpty()) {
                        selectedProjectId = projects[0].id
                        fetchBeneficiaries(selectedProjectId!!)
                    }
                } else {
                    showToast("Failed to load projects")
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                showToast("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun fetchBeneficiaries(projectId: Long) {
        api.getBeneficiariesByProject(projectId).enqueue(object : Callback<List<Beneficiary>> {
            override fun onResponse(call: Call<List<Beneficiary>>, response: Response<List<Beneficiary>>) {
                if (response.isSuccessful && response.body() != null) {
                    beneficiaries = response.body()!!
                    val beneficiaryNames = beneficiaries.map { it.name ?: "Unnamed" }
                    val adapter = ArrayAdapter(this@AddAllocationActivity, android.R.layout.simple_spinner_item, beneficiaryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerBeneficiary.adapter = adapter
                    if (beneficiaries.isNotEmpty()) {
                        selectedBeneficiaryId = beneficiaries[0].id
                    }
                } else {
                    showToast("Failed to load beneficiaries")
                }
            }
            override fun onFailure(call: Call<List<Beneficiary>>, t: Throwable) {
                showToast("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun fetchStaff() {
        api.getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    staff = response.body()!!.filter { it.role == "STAFF" }
                    val staffNames = staff.map { it.username ?: "Unnamed" }
                    val adapter = ArrayAdapter(this@AddAllocationActivity, android.R.layout.simple_spinner_item, staffNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerStaff.adapter = adapter
                    if (staff.isNotEmpty()) {
                        selectedStaffId = staff[0].id
                    }
                } else {
                    showToast("Failed to load staff")
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showToast("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun submitAllocation() {
        val amountText = editTextAmount.text.toString()
        val amount = amountText.toDoubleOrNull()
        if (selectedProjectId == null || selectedBeneficiaryId == null || selectedStaffId == null || amount == null) {
            showToast("Please fill all fields correctly")
            return
        }
        btnSubmit.isEnabled = false
        api.addAllocation(selectedProjectId!!, selectedBeneficiaryId!!, amount, selectedStaffId!!)
            .enqueue(object : Callback<Allocation> {
                override fun onResponse(call: Call<Allocation>, response: Response<Allocation>) {
                    btnSubmit.isEnabled = true
                    if (response.isSuccessful) {
                        showToast("Allocation added successfully")
                        finish()
                    } else {
                        showToast("Failed to add allocation")
                    }
                }
                override fun onFailure(call: Call<Allocation>, t: Throwable) {
                    btnSubmit.isEnabled = true
                    showToast("Network error: ${t.localizedMessage}")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
} 