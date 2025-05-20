package com.example.foodalergy.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.foodalergy.R
import com.example.foodalergy.data.model.AllergyUpdateRequest
import com.example.foodalergy.data.model.User
import com.example.foodalergy.data.network.RetrofitClient
import com.example.foodalergy.data.store.UserSessionManager
import com.example.foodalergy.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var textName: TextView
    private lateinit var textUsername: TextView
    private lateinit var editAllergies: EditText
    private lateinit var btnEditAllergies: Button
    private lateinit var btnSaveAllergies: Button
    private lateinit var btnLogout: Button

    private lateinit var session: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textName = findViewById(R.id.textName)
        textUsername = findViewById(R.id.textUsername)
        editAllergies = findViewById(R.id.editAllergies)
        btnEditAllergies = findViewById(R.id.btnEditAllergies)
        btnSaveAllergies = findViewById(R.id.btnSaveAllergies)
        btnLogout = findViewById(R.id.btnLogout)

        editAllergies.isEnabled = false
        btnSaveAllergies.isEnabled = false

        session = UserSessionManager(this)

        // ✅ Appel correct sans paramètre
        fetchUserProfile()

        btnEditAllergies.setOnClickListener {
            editAllergies.isEnabled = true
            btnSaveAllergies.isEnabled = true
            btnEditAllergies.isEnabled = false
        }

        btnSaveAllergies.setOnClickListener {
            val updatedAllergies = editAllergies.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val userId = session.getUserId()
            updateUserAllergies(userId, updatedAllergies)
        }

        btnLogout.setOnClickListener {
            session.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    private fun fetchUserProfile() {
        val session = UserSessionManager(this) // ✅ créer une instance
        val username = session.getUsername()
        val test = UserSessionManager(this).getUsername()// ✅ appeler via instance

        if (test.isEmpty()) {
            Toast.makeText(this, "Aucun nom d'utilisateur en session", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Username: $test", Toast.LENGTH_SHORT).show()

        RetrofitClient.userApi.getUserByUsername(test).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    textName.text = user.name ?: ""
                    textUsername.text = user.username ?: ""
                    editAllergies.setText(user.allergies?.joinToString(", ") ?: "")
                    // ✅ Sauvegarder userId pour les futures appels
                    session.saveSession(username, session.getToken(), user.id ?: "")
                } else {
                    Toast.makeText(this@ProfileActivity, "Erreur lors du chargement du profil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Erreur réseau : ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateUserAllergies(userId: String, allergies: List<String>) {
        if (userId.isEmpty()) {
            Toast.makeText(this, "UserId manquant", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.userApi.updateUserAllergies(userId, AllergyUpdateRequest(allergies))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Allergies mises à jour!", Toast.LENGTH_SHORT).show()
                        editAllergies.isEnabled = false
                        btnSaveAllergies.isEnabled = false
                        btnEditAllergies.isEnabled = true
                    } else {
                        Toast.makeText(this@ProfileActivity, "Échec de la mise à jour", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Erreur réseau", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
