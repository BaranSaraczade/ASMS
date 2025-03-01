package com.example.asms.model

import java.io.Serializable

data class CampaignStats(
    val total: Int = 0,
    val delivered: Int = 0,
    val failed: Int = 0,
    val pending: Int = 0,
    val spam: Int = 0,
    val successRate: Float = 0f
) : Serializable

data class CampaignMessage(
    val id: Int = 0,
    val campaignId: Int = 0,
    val message: String = "",
    val charCount: Int = 0
) : Serializable