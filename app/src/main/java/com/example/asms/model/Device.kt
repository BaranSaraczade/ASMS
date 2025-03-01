package com.example.asms.model

data class Device(
    val id: Int,
    val user_id: Int,
    val device_id: String,
    val phone_number: String,
    val device_name: String?,
    val device_model: String?,
    val last_active: String?,
    val status: Int,
    val created_at: String
)