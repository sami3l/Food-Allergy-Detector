package com.example.foodalergy.data.model

data class HistoryItem(
    val productName: String,
    val riskLevel: String // e.g., "✔️ Safe", "❌ Contains Milk", etc.
)
