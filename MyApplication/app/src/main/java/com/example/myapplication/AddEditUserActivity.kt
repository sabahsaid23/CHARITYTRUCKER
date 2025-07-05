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

class AddEditUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_user)

        val usernameInput: EditText = findViewById(R.id.userUsernameInput)
        val emailInput: EditText = findViewById(R.id.userEmailInput)
        val roleSpinner: Spinner = findViewById(R.id.userRoleSpinner)
        val statusSpinner: Spinner = findViewById(R.id.userStatusSpinner)
        val saveButton: Button = findViewById(R.id.saveUserButton)
        val deleteButton: Button = findViewById(R.id.deleteUserButton)
        val passwordInput: EditText = findViewById(R.id.userPasswordInput)

        val roles = arrayOf("ADMIN", "STAFF", "DONOR")
        val statuses = arrayOf("ACTIVE", "INACTIVE")
        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        val mode = intent.getStringExtra("mode")
        val userId = intent.getLongExtra("user_id", -1L)
        val isEdit = userId != -1L

        if (mode == "add_staff") {
            // Add staff mode
            saveButton.text = "Add Staff"
            deleteButton.visibility = View.GONE
            passwordInput.visibility = View.VISIBLE
        } else if (isEdit) {
            // Edit mode
            usernameInput.setText(intent.getStringExtra("username") ?: "")
            emailInput.setText(intent.getStringExtra("email") ?: "")
            val role = intent.getStringExtra("role") ?: "STAFF"
            val status = intent.getStringExtra("status") ?: "ACTIVE"
            val roleIndex = roles.indexOf(role)
            val statusIndex = statuses.indexOf(status)
            if (roleIndex >= 0) roleSpinner.setSelection(roleIndex)
            if (statusIndex >= 0) statusSpinner.setSelection(statusIndex)
            saveButton.text = "Update User"
            deleteButton.visibility = View.VISIBLE
            passwordInput.visibility = View.VISIBLE
        } else {
            // Default: hide delete
            deleteButton.visibility = View.GONE
            passwordInput.visibility = View.GONE
        }

        saveButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()
            val status = statusSpinner.selectedItem.toString()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.78.83.106:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(AdminApi::class.java)

            if (mode == "add_staff") {
                // Register staff
                val staffRequest = RegisterRequest(username, email, password, "STAFF")
                api.registerStaff(staffRequest).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddEditUserActivity, "Staff added!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddEditUserActivity, "Failed to add staff", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(this@AddEditUserActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else if (isEdit) {
                val updateRequest = RegisterRequest(username, email, password, role)
                api.updateUser(userId, updateRequest).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddEditUserActivity, "User updated!", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@AddEditUserActivity, "Failed to update user", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(this@AddEditUserActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
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
                api.deleteUser(userId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddEditUserActivity, "User deleted!", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@AddEditUserActivity, "Failed to delete user", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@AddEditUserActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
} 