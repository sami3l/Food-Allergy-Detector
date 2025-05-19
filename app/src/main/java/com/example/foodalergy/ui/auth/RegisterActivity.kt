package com.example.foodalergy.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.foodalergy.R
import com.example.foodalergy.data.model.RegisterRequest
import com.example.foodalergy.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var allergiesEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginLink: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        nameEditText = findViewById(R.id.editTextName)
        emailEditText = findViewById(R.id.editTextEmail)
        allergiesEditText = findViewById(R.id.editTextAllergies)
        signUpButton = findViewById(R.id.buttonSignUp)
        loginLink = findViewById(R.id.textLoginLink)
        progressBar = findViewById(R.id.progressBar)

        progressBar.visibility = ProgressBar.INVISIBLE

        signUpButton.setOnClickListener {
            registerUser()
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val allergiesRaw = allergiesEditText.text.toString().trim()
        val allergies = if (allergiesRaw.isNotEmpty()) allergiesRaw.split(",").map { it.trim() } else listOf()

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = ProgressBar.VISIBLE

        val registerRequest = RegisterRequest(
            username = username,
            password = password,
            name = name,
            email = email,
            allergies = allergies
        )

        RetrofitClient.authApi.register(registerRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressBar.visibility = ProgressBar.INVISIBLE
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Sign up successful! Please sign in.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Sign up failed: " + response.code(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressBar.visibility = ProgressBar.INVISIBLE
                Toast.makeText(this@RegisterActivity, "Sign up failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })}
}
