package com.useapi.foodallergydetector.data.network

import com.example.foodalergy.data.model.AuthRequest
import com.example.foodalergy.data.model.AuthResponse
import com.example.foodalergy.data.model.RegisterRequest
import com.example.foodalergy.data.model.User
import retrofit2.Call
import retrofit2.http.*

interface AuthApi {

    @POST("api/auth/login")
    fun login(@Body authRequest: AuthRequest): Call<AuthResponse>


    @POST("api/users/register")
    fun register(@Body user: RegisterRequest): Call<Void>

    @GET("api/users/by-username/{username}")
    fun getUserByUsername(@Path("username") username: String): Call<User>

}
