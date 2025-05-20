package com.example.foodalergy.data.model

data class EvaluateResponse(
    val allergens: List<String>,
    val riskLevel: String,
    val productName: String,
    val imageUrl: String,
    val source: String,
    val timestamp: String
)
