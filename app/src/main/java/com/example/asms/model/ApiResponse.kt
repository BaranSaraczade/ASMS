package com.example.asms.model

import java.io.Serializable

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: DeviceData?
)

data class CampaignListResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: List<Campaign> = emptyList()
) : Serializable

data class CampaignDetailResponse(
    val success: Boolean = false,
    val campaign: Campaign? = null,
    val stats: CampaignStats? = null,
    val messages: List<CampaignMessage> = emptyList()
) : Serializable

data class CampaignActionRequest(
    val campaign_id: Int = 0
) : Serializable

data class CampaignPauseRequest(
    val campaign_id: Int = 0,
    val duration: Int = 0,
    val timed: Boolean = false,
    val manual: Boolean = false
) : Serializable
