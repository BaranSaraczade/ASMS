
package com.example.asms.model

data class CampaignListResponse(
    val success: Boolean,
    val data: List<Campaign> = emptyList()
)