package com.example.foodalergy.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodalergy.R
import com.example.foodalergy.data.store.UserSessionManager
import com.example.foodalergy.ui.evaluate.EvaluationActivity
import com.example.foodalergy.ui.user.ProfileActivity
import com.example.foodalergy.ui.user.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FoodAllergy)
        setContentView(R.layout.activity_main)

        findViewById<BottomNavigationView>(R.id.bottomNav)
            .setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        // nothing to do: we’re already here
                        true
                    }
                    R.id.nav_scan -> {
                        startActivity(Intent(this, EvaluationActivity::class.java))
                        true
                    }
                    R.id.nav_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
    }
}