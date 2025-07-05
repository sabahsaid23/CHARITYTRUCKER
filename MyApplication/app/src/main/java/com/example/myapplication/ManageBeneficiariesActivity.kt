package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
import android.widget.ArrayAdapter

class ManageBeneficiariesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var addBeneficiaryButton: Button
    private lateinit var adapter: BeneficiaryAdapter
    private val beneficiaries = mutableListOf<Beneficiary>()
    private val BASE_URL = "http://10.78.83.106:8080"
    private var projectList: List<Project> = listOf()
    private val statusOptions = arrayOf("ACTIVE", "INACTIVE", "N/A")
    private var staffId: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_beneficiaries)
        staffId = intent.getLongExtra("staff_id", 1L)

        recyclerView = findViewById(R.id.beneficiariesRecyclerView)
        emptyText = findViewById(R.id.emptyText)
        addBeneficiaryButton = findViewById(R.id.addBeneficiaryButton)
        adapter = BeneficiaryAdapter(beneficiaries) { beneficiary ->
            showEditDeleteDialog(beneficiary)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addBeneficiaryButton.setOnClickListener {
            showAddBeneficiaryDialog()
        }

        fetchProjects()
        fetchBeneficiaries()
    }

    override fun onResume() {
        super.onResume()
        fetchBeneficiaries()
    }

    private fun fetchBeneficiaries() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(StaffApi::class.java)
        api.getAllBeneficiaries().enqueue(object : Callback<List<Beneficiary>> {
            override fun onResponse(call: Call<List<Beneficiary>>, response: Response<List<Beneficiary>>) {
                if (response.isSuccessful && response.body() != null) {
                    beneficiaries.clear()
                    beneficiaries.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    emptyText.visibility = if (beneficiaries.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this@ManageBeneficiariesActivity, "Failed to load beneficiaries", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Beneficiary>>, t: Throwable) {
                Toast.makeText(this@ManageBeneficiariesActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
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
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {}
        })
    }

    private fun showAddBeneficiaryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_beneficiary, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.beneficiaryNameInput)
        val contactInput = dialogView.findViewById<EditText>(R.id.beneficiaryContactInput)
        val projectSpinner = dialogView.findViewById<Spinner>(R.id.projectSpinner)
        val statusSpinner = dialogView.findViewById<Spinner>(R.id.statusSpinner)
        val projectNames = projectList.map { it.name ?: "Unnamed" }
        projectSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNames)
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        AlertDialog.Builder(this)
            .setTitle("Add Beneficiary")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val contact = contactInput.text.toString().trim()
                val selectedProjectIndex = projectSpinner.selectedItemPosition
                val selectedStatus = statusOptions[statusSpinner.selectedItemPosition]
                if (name.isNotEmpty() && projectList.isNotEmpty()) {
                    val projectId = projectList[selectedProjectIndex].id!!
                    val beneficiary = Beneficiary(null, name, contact, selectedStatus)
                    addBeneficiary(beneficiary, projectId, staffId)
                } else {
                    Toast.makeText(this, "Name and project required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDeleteDialog(beneficiary: Beneficiary) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_beneficiary, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.beneficiaryNameInput)
        val contactInput = dialogView.findViewById<EditText>(R.id.beneficiaryContactInput)
        val projectSpinner = dialogView.findViewById<Spinner>(R.id.projectSpinner)
        val statusSpinner = dialogView.findViewById<Spinner>(R.id.statusSpinner)
        nameInput.setText(beneficiary.name)
        contactInput.setText(beneficiary.contact)
        val projectNames = projectList.map { it.name ?: "Unnamed" }
        projectSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNames)
        val currentProjectIndex = projectList.indexOfFirst { it.id == beneficiary.id }
        if (currentProjectIndex >= 0) projectSpinner.setSelection(currentProjectIndex)
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        val currentStatusIndex = statusOptions.indexOf(beneficiary.status ?: "ACTIVE")
        if (currentStatusIndex >= 0) statusSpinner.setSelection(currentStatusIndex)
        AlertDialog.Builder(this)
            .setTitle("Edit/Delete Beneficiary")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = nameInput.text.toString().trim()
                val contact = contactInput.text.toString().trim()
                val selectedIndex = projectSpinner.selectedItemPosition
                val selectedStatus = statusOptions[statusSpinner.selectedItemPosition]
                if (name.isNotEmpty() && beneficiary.id != null && projectList.isNotEmpty()) {
                    val projectId = projectList[selectedIndex].id!!
                    updateBeneficiary(beneficiary.id, Beneficiary(beneficiary.id, name, contact, selectedStatus), projectId)
                } else {
                    Toast.makeText(this, "Name and project required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Delete") { _, _ ->
                if (beneficiary.id != null) {
                    deleteBeneficiary(beneficiary.id)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addBeneficiary(beneficiary: Beneficiary, projectId: Long, addedById: Long) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(StaffApi::class.java)
        api.addBeneficiary(beneficiary, projectId, addedById).enqueue(object : Callback<Beneficiary> {
            override fun onResponse(call: Call<Beneficiary>, response: Response<Beneficiary>) {
                if (response.isSuccessful) {
                    fetchBeneficiaries()
                    Toast.makeText(this@ManageBeneficiariesActivity, "Beneficiary added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageBeneficiariesActivity, "Failed to add beneficiary", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Beneficiary>, t: Throwable) {
                Toast.makeText(this@ManageBeneficiariesActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBeneficiary(id: Long, beneficiary: Beneficiary, projectId: Long) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(StaffApi::class.java)
        api.updateBeneficiary(id, beneficiary, projectId, staffId).enqueue(object : Callback<Beneficiary> {
            override fun onResponse(call: Call<Beneficiary>, response: Response<Beneficiary>) {
                if (response.isSuccessful) {
                    fetchBeneficiaries()
                    Toast.makeText(this@ManageBeneficiariesActivity, "Beneficiary updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageBeneficiariesActivity, "Failed to update beneficiary", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Beneficiary>, t: Throwable) {
                Toast.makeText(this@ManageBeneficiariesActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteBeneficiary(id: Long) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(StaffApi::class.java)
        api.deleteBeneficiary(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchBeneficiaries()
                    Toast.makeText(this@ManageBeneficiariesActivity, "Beneficiary deleted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageBeneficiariesActivity, "Failed to delete beneficiary", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ManageBeneficiariesActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 