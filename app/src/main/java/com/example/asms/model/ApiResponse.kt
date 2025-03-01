package com.example.asms.model

import java.io.Serializable

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: DeviceData?
)



data class CampaignDetailResponse(
    val success: Boolean,
    val campaign: Campaign? = null,
    val stats: CampaignStats? = null,
    val messages: List<CampaignMessage> = emptyList()
)

data class CampaignActionRequest(
    val campaign_id: Int
)

data class CampaignPauseRequest(
    val campaign_id: Int,
    val duration: Int = 0,
    val timed: Boolean = false,
    val manual: Boolean = false
)

