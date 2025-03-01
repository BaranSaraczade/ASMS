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

    // Kampanya listeleme API çağrısı
    @GET("api/campaign/list.php")
    fun getCampaigns(@Header("USER_ID") userId: Int): Call<CampaignListResponse>

    // Kampanya detayları API çağrısı
    @GET("api/campaign/get_stats.php")
    fun getCampaignDetails(@Query("id") campaignId: Int): Call<CampaignDetailResponse>

    // Kampanyayı başlatma API çağrısı
    @POST("api/campaign/start.php")
    fun startCampaign(@Body requestBody: CampaignActionRequest): Call<ApiResponse>

    // Kampanyayı durdurma API çağrısı
    @POST("api/campaign/stop.php")
    fun stopCampaign(@Body requestBody: CampaignActionRequest): Call<ApiResponse>

    // Kampanyayı duraklatma API çağrısı
    @POST("api/campaign/pause.php")
    fun pauseCampaign(@Body requestBody: CampaignPauseRequest): Call<ApiResponse>
}