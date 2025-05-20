package com.example.foodalergy.data.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val name: String?,
    val allergies: List<String> = listOf(),
    val role: String = "USER"
)
