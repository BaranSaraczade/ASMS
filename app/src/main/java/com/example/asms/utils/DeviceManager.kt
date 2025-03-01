package com.example.asms.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import androidx.core.app.ActivityCompat
import com.example.asms.api.RetrofitClient
import com.example.asms.model.DeviceRegistration
import com.example.asms.model.DeviceResponse

class DeviceManager(private val context: Context) {

    suspend fun registerDevice(userId: Int) {
        try {
            val subscriptionManager = context.getSystemService(SubscriptionManager::class.java)

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                subscriptionManager.activeSubscriptionInfoList?.forEach { subscription ->
                    val deviceId = "${Build.DEVICE}_${subscription.iccId}_${subscription.simSlotIndex}"
                    val request = DeviceRegistration(
                        user_id = userId,
                        device_id = deviceId,
                        phone_number = subscription.number ?: "Unknown",
                        device_name = subscription.carrierName?.toString(),
                        device_model = Build.MODEL
                    )

                    val response = RetrofitClient.apiService.registerDevice(request)
                    if (response.isSuccessful) {
                        response.body()?.let { deviceResponse ->
                            if (deviceResponse.success) {
                                // Cihaz ID'yi kaydet
                                deviceResponse.data?.let { deviceData ->
                                    PrefsManager.saveDeviceId(deviceData.device_id)
                                }
                            } else {
                                throw Exception(deviceResponse.message)
                            }
                        } ?: throw Exception("Boş response")
                    } else {
                        throw Exception("Kayıt başarısız: ${response.message()}")
                    }
                }
            } else {
                throw Exception("Telefon durumu izni gerekli")
            }
        } catch (e: Exception) {
            throw Exception("Cihaz kaydedilemedi: ${e.message}")
        }
    }

    fun getPhonePermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
        )
    }

    fun hasPhonePermissions(): Boolean {
        return getPhonePermissions().all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}