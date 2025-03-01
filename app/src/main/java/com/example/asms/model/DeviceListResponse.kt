package com.example.asms.model

data class DeviceListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Device>?
)