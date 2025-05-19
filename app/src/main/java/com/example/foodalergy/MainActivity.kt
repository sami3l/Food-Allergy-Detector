package com.example.foodalergy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodalergy.R
import com.example.foodalergy.ui.auth.LoginActivity
import com.example.foodalergy.ui.evaluate.EvaluationActivity
import com.example.foodalergy.ui.user.ProfileActivity
import com.example.foodalergy.ui.scan.ScanActivity
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

        // --- Retrieve user name from SharedPreferences (example key: "user_name") ---
        val prefs = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val username = prefs.getString(LoginActivity.KEY_USERNAME, null)

        textWelcome.text = if (!username.isNullOrEmpty()) "Hello, $username!" else "Hello, User!"

        Toast.makeText(this, "Username in prefs: $username", Toast.LENGTH_LONG).show()

        // --- Open ProfileActivity ---
        buttonProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // --- Open SettingsActivity ---
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // --- Open ScanActivity ---
        buttonScan.setOnClickListener {
            val intent = Intent(this, EvaluationActivity::class.java)
            startActivity(intent)
        }
    }
}
