package com.example.foodalergy.data.model

data class ProductInfoResponse(
    val id: String,
    val productName: String,
    val ingredients: String,
    val barcode: String,
    val description: String,
    val imageUrl: String?,
    val allergens: List<String>
) {

}