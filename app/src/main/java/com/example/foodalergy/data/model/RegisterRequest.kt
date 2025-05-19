// File: data/model/RegisterRequest.kt
package com.example.foodalergy.data.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val name: String,
    val email: String,
    val allergies: List<String> = listOf(),
    val role: String = "USER"
)
