package com.example.foodalergy.data.network

import com.example.foodalergy.data.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("/api/users/by-username/{username}")
    fun getUserByUsername(@Path("username") username: String): Call<User>


}