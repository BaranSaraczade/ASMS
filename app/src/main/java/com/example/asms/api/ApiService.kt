package com.example.asms.api

import com.example.asms.model.LoginRequest
import com.example.asms.model.LoginResponse
import com.example.asms.model.DeviceRegistration
import com.example.asms.model.ApiResponse
import com.example.asms.model.DeviceListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header


interface ApiService {
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("device/register.php")
    suspend fun registerDevice(@Body request: DeviceRegistration): Response<ApiResponse>

    @GET("device/list.php")
    suspend fun getUserDevices(@Header("USER-ID") userId: Int): Response<DeviceListResponse>
}