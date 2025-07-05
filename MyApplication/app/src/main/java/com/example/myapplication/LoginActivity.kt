package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var errorMsg: TextView
    private lateinit var registerLink: TextView

    // TODO: Replace with your backend base URL
    private val BASE_URL = "http://10.78.83.106:8080" // 10.0.2.2 for Android emulator localhost

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        errorMsg = findViewById(R.id.errorMsg)
        registerLink = findViewById(R.id.registerLink)

        loginButton.setOnClickListener {
            errorMsg.visibility = View.GONE
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                errorMsg.text = "Please enter email and password."
                errorMsg.visibility = View.VISIBLE
            }
        }

        registerLink.setOnClickListener {
            // TODO: Replace with your RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AuthApi::class.java)
        val request = LoginRequest(email, password)
        api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val role = user.role?.lowercase()
                    if (role != null) {
                        when (role) {
                           "admin" -> navigateToDashboard(AdminDashboardActivity::class.java, user.id, user.username, user.role)
                            "donor" -> navigateToDashboard(DonorDashboardActivity::class.java, user.id, user.username, user.role)
                            "staff" -> navigateToDashboard(StaffDashboardActivity::class.java, user.id, user.username, user.role)
                            else -> showError("Unknown user role: $role")
                        }
                    } else {
                        showError("Login succeeded but user role is missing.")
                    }
                } else {
                    val errorText = try {
                        response.errorBody()?.string() ?: "Invalid email or password."
                    } catch (e: Exception) {
                        "Invalid email or password."
                    }
                    showError(errorText)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun showError(message: String) {
        errorMsg.text = message
        errorMsg.visibility = View.VISIBLE
    }

    private fun navigateToDashboard(activityClass: Class<*>, userId: Long? = null, username: String? = null, role: String? = null) {
        val intent = Intent(this, activityClass)
        if (userId != null) intent.putExtra("staff_id", userId)
        if (username != null) intent.putExtra("username", username)
        if (role != null) intent.putExtra("role", role)
        if (activityClass == DonorDashboardActivity::class.java && userId != null) {
            intent.putExtra("donor_id", userId)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 