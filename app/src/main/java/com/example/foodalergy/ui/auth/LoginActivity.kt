package com.example.foodalergy.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodalergy.R
import com.example.foodalergy.data.model.AuthRequest
import com.example.foodalergy.data.model.AuthResponse
import com.example.foodalergy.data.network.RetrofitClient
import com.example.foodalergy.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "user_prefs"
        const val KEY_TOKEN = "token"
        const val KEY_USERNAME = "username"
        const val KEY_USERID = "userId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Connexion - Food Allergy Detector"

        val userNameField = findViewById<EditText>(R.id.username)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginBtn.setOnClickListener {
            val username = userNameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Optional: Hide the keyboard after pressing login
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(loginBtn.windowToken, 0)

            val request = AuthRequest(username = username, password = password)

            RetrofitClient.authApi.login(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val authResponse = response.body()!!
                        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        prefs.edit().apply {
                            putString(KEY_TOKEN, authResponse.token)
                            putString(KEY_USERNAME, authResponse.username) // <--- this comes from backend now!
                            putString(KEY_USERID, authResponse.userId)
                            apply()
                        }
                        Toast.makeText(this@LoginActivity, "Connecté !", Toast.LENGTH_SHORT).show()
                        // Go to Home/MainActivity after login
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Identifiants incorrects ou compte inexistant", Toast.LENGTH_SHORT).show()
                        passwordField.text.clear()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Erreur réseau : ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
