package com.example.asms.models

import java.io.Serializable

data class Campaign(
    val id: Int = 0,
    val userId: Int = 0,
    val deviceId: Int = 0,
    val categoryId: Int = 0,
    val campaignName: String = "",
    val message: String = "",
    val totalNumbers: Int = 0,
    val sentNumbers: Int = 0,
    val failedNumbers: Int = 0,
    val spamNumbers: Int = 0,
    val status: Int = 0, // 0=işlemde, 1=tamamlandı, 2=başarısız, 3=iptal edildi
    val createdAt: String = "",
    val completedAt: String? = null,
    val deviceName: String = "",
    val phoneNumber: String = ""
) : Serializable {

    // Status metinleri ve renklerini döndüren yardımcı metodlar
    fun getStatusText(): String {
        return when(status) {
            0 -> "İşlemde"
            1 -> "Tamamlandı"
            2 -> "Başarısız"
            3 -> "İptal Edildi"
            else -> "Bilinmiyor"
        }
    }

    fun getStatusColor(): String {
        return when(status) {
            0 -> "#FFC107" // warning (sarı)
            1 -> "#28A745" // success (yeşil)
            2 -> "#DC3545" // danger (kırmızı)
            3 -> "#6C757D" // secondary (gri)
            else -> "#6C757D" // secondary (gri)
        }
    }

    // İlerleme yüzdesini hesapla
    fun getProgressPercentage(): Int {
        if (totalNumbers == 0) return 0
        return ((sentNumbers * 100.0f) / totalNumbers).toInt()
    }
}