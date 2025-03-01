// model/LoginResponse.kt
package com.example.asms.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?  // Nullable olarak işaretlendi
)

data class UserData(
    val id: Int,
    val username: String,
    val email: String,
    val credits: Int
)