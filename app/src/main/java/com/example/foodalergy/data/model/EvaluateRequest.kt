package com.example.foodalergy.data.model

data class EvaluateRequest(
    val productText: String,
    val barcode: String,
    val productName: String
)

