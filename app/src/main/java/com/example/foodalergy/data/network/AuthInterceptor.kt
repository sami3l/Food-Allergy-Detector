package com.example.foodalergy.data.network

import android.content.Context
import com.example.foodalergy.data.store.UserSessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val session = UserSessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = session.getToken()
        val request = if (token.isNotEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
