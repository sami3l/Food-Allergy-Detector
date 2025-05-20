package com.example.foodalergy.data.network



import com.example.foodalergy.data.model.EvaluateRequest
import com.example.foodalergy.data.model.EvaluateResponse
import com.example.foodalergy.data.model.HistoryItem
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

    @GET("/api/user/{userId}/history")
    fun getHistory(
        @Path("userId") userId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<List<HistoryItem>>



}
