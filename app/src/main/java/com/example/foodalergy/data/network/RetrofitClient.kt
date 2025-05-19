package com.example.foodalergy.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.foodalergy.data.network.ScanApi
import com.example.foodalergy.data.network.UserApi
import com.useapi.foodallergydetector.data.network.AuthApi

object RetrofitClient {

    // Change this BASE_URL if running on a real device (use your PC's local IP instead)
    private const val BASE_URL = "http://192.168.11.128:8082/" // Note the trailing slash for Retrofit best practice

    private val retrofit: Retrofit by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API endpoints
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val scanApi: ScanApi by lazy {
        retrofit.create(ScanApi::class.java)
    }

    val userApi: UserApi by lazy { // fixed name (lowercase)
        retrofit.create(UserApi::class.java)
    }

    val allergyApi: AllergyApi by lazy {
        retrofit.create(AllergyApi::class.java)
    }
}

