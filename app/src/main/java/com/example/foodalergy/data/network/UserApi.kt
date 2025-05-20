package com.example.foodalergy.data.network

import com.example.foodalergy.data.model.AllergyUpdateRequest
import com.example.foodalergy.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("/api/users/by-username/{username}")
    fun getUserByUsername(@Path("username") username: String): Call<User>

    @PUT("/api/user/{userId}/allergies")
    fun updateUserAllergies(
        @Path("userId") userId: String,
        @Body request: AllergyUpdateRequest
    ): Call<Void>

}
