package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var fullNameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var errorMsg: TextView
    private lateinit var loginLink: TextView

    private val BASE_URL = "http://10.78.83.106:8080"  // Use your laptop's WiFi IP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        fullNameInput = findViewById(R.id.fullNameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        registerButton = findViewById(R.id.registerButton)
        errorMsg = findViewById(R.id.errorMsg)
        loginLink = findViewById(R.id.loginLink)

        registerButton.setOnClickListener {
            errorMsg.visibility = View.GONE
            val fullName = fullNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showError("Please fill in all fields.")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                showError("Passwords do not match.")
                return@setOnClickListener
            }
            registerUser(fullName, email, password)
        }

        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(fullName: String, email: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AuthApi::class.java)
        val request = RegisterRequest(username = fullName, email = email, password = password)
        // Use the same AuthApi but add this method:
        // @POST("/api/users/register")
        // fun register(@Body request: RegisterRequest): Call<LoginResponse>
        val call = api.registerDonor(request)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Registration successful, go to login
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val errorText = response.errorBody()?.string() ?: "Registration failed."
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
} 