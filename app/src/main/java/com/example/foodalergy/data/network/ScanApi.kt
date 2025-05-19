package com.example.foodalergy.data.network


import com.example.foodalergy.data.model.EvaluateRequest
import com.example.foodalergy.data.model.EvaluateResponse
import com.example.foodalergy.data.model.ProductResponse
import retrofit2.Call
import retrofit2.http.*

interface ScanApi {

    @GET("/api/scan/{scanId}")
    fun getScan(@Path("scanId") scanId: String): Call<ProductResponse>

    @POST("/api/evaluate")
    fun evaluate(@Body request: EvaluateRequest): Call<EvaluateResponse>

    @DELETE("/api/scan/{scanId}")
    fun deleteScan(@Path("scanId") scanId: String): Call<Void>
}
