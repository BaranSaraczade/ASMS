package com.example.asms.model

data class DeviceRegistration(
    val user_id: Int,
    val device_id: String,
    val phone_number: String,
    val device_name: String?,
    val device_model: String
)

data class DeviceData(
    val id: Int,
    val device_id: String,
    val device_name: String
)

data class DeviceResponse(
    val success: Boolean,
    val message: String,
    val data: DeviceData?
)