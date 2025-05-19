package com.example.foodalergy.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsetsAnimation
import retrofit2.Callback
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodalergy.R
import com.example.foodalergy.data.model.AuthRequest
import com.example.foodalergy.data.model.AuthResponse
import com.example.foodalergy.data.model.User
import com.example.foodalergy.data.network.RetrofitClient
import com.example.foodalergy.ui.evaluate.EvaluationActivity
import retrofit2.Response
import retrofit2.Call


class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Connexion - Food Allergy Detector"

        val UserNameField = findViewById<EditText>(R.id.username)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)


        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val request = AuthRequest(
                username = UserNameField.text.toString(),
                password = passwordField.text.toString()
            )


            RetrofitClient.authApi.login(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        val userId = response.body()?.userId
                        Toast.makeText(this@LoginActivity, "Connecté ", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, EvaluationActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Identifiants incorrects ", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Erreur réseau", Toast.LENGTH_SHORT).show()
                }
            })


        }
    }


}
