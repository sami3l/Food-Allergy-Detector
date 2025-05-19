package com.example.foodalergy.data.network


import com.example.foodalergy.data.network.ScanApi
import com.useapi.foodallergydetector.data.network.AuthApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {

    private const val BASE_URL = "http://192.168.120.78:8082" // Pour Android Emulator

    // Retrofit instance partagée
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

    // Interfaces API
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val scanApi: ScanApi by lazy {
        retrofit.create(ScanApi::class.java)
    }
}
