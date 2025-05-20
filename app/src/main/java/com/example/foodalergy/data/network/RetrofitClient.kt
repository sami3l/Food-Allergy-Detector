package com.example.foodalergy.data.network

import android.content.Context
import com.example.foodalergy.App
import com.example.foodalergy.data.network.ScanApi
import com.example.foodalergy.data.network.UserApi
import com.example.foodalergy.data.network.AllergyApi
import com.useapi.foodallergydetector.data.network.AuthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.11.128:8082/" // ✅ ton IP locale pour tests

    // Retrofit singleton avec client sécurisé
    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(App.context)) // ✅ injecte le token dynamiquement
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Services
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val scanApi: ScanApi by lazy {
        retrofit.create(ScanApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val allergyApi: AllergyApi by lazy {
        retrofit.create(AllergyApi::class.java)
    }
}
