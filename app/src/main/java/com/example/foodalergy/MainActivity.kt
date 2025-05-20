package com.example.foodalergy.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodalergy.R
import com.example.foodalergy.data.store.UserSessionManager
import com.example.foodalergy.ui.evaluate.EvaluationActivity
import com.example.foodalergy.ui.user.ProfileActivity
import com.example.foodalergy.ui.user.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        val buttonProfile = findViewById<ImageButton>(R.id.buttonProfile)
        val buttonSettings = findViewById<ImageButton>(R.id.buttonSettings)
        val buttonScan = findViewById<Button>(R.id.buttonScan)
        val textWelcome = findViewById<TextView>(R.id.textWelcome)

        // --- Retrieve user name using UserSessionManager ---
        val username = UserSessionManager(this).getUsername()

        textWelcome.text = if (username.isNotEmpty()) "Hello, $username!" else "Hello, User!"

        // You can remove this Toast if you want
        Toast.makeText(this, "Username in prefs: $username", Toast.LENGTH_LONG).show()

        // --- Open ProfileActivity ---
        buttonProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // --- Open SettingsActivity ---
        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // --- Open EvaluationActivity ---
        buttonScan.setOnClickListener {
            startActivity(Intent(this, EvaluationActivity::class.java))
        }
    }
}
