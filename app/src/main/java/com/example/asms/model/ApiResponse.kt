package com.example.asms.model

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: DeviceData?
)

