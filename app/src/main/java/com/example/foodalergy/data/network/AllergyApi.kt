package com.example.foodalergy.data.network

import com.example.foodalergy.data.model.AllergyUpdateRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface AllergyApi {
    @PUT("/api/user/{userId}/allergies")
    fun updateAllergies(
        @Path("userId") userId: String,
        @Body request: AllergyUpdateRequest
    ): Call<Void>
}
