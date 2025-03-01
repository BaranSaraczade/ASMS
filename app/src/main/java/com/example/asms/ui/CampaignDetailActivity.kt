package com.example.asms.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.asms.R
import com.example.asms.api.RetrofitClient
import com.example.asms.model.ApiResponse
import com.example.asms.model.Campaign
import com.example.asms.model.CampaignActionRequest
import com.example.asms.model.CampaignPauseRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignDetailActivity : AppCompatActivity() {

    private lateinit var tvCampaignName: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvDevice: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTotalSms: TextView
    private lateinit var tvSentSms: TextView
    private lateinit var tvPendingSms: TextView
    private lateinit var tvFailedSms: TextView
    private lateinit var tvSpamSms: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button
    private lateinit var campaign: Campaign

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_detail)

        // Intent'tan kampanya bilgisini al
        campaign = intent.getSerializableExtra("campaign") as Campaign

        // View'ları bul
        tvCampaignName = findViewById(R.id.tvCampaignName)
        tvStatus = findViewById(R.id.tvStatus)
        tvDevice = findViewById(R.id.tvDevice)
        tvDate = findViewById(R.id.tvDate)
        tvTotalSms = findViewById(R.id.tvTotalSms)
        tvSentSms = findViewById(R.id.tvSentSms)
        tvPendingSms = findViewById(R.id.tvPendingSms)
        tvFailedSms = findViewById(R.id.tvFailedSms)
        tvSpamSms = findViewById(R.id.tvSpamSms)
        progressBar = findViewById(R.id.progressBar)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        btnStop = findViewById(R.id.btnStop)

        // Kampanya bilgilerini göster
        displayCampaignDetails()

        // Buton tıklama olaylarını ayarla
        setupButtonListeners()
    }

    private fun displayCampaignDetails() {
        tvCampaignName.text = campaign.campaignName
        tvStatus.text = campaign.getStatusText()
        tvStatus.setTextColor(Color.parseColor(campaign.getStatusColor()))
        tvDevice.text = "${campaign.deviceName} (${campaign.phoneNumber})"
        tvDate.text = "Başlangıç: ${campaign.createdAt}"
        tvTotalSms.text = "${campaign.totalNumbers}"
        tvSentSms.text = "${campaign.sentNumbers}"
        tvPendingSms.text = "${campaign.totalNumbers - campaign.sentNumbers - campaign.failedNumbers - campaign.spamNumbers}"
        tvFailedSms.text = "${campaign.failedNumbers}"
        tvSpamSms.text = "${campaign.spamNumbers}"

        // Progress bar
        progressBar.max = 100
        progressBar.progress = campaign.getProgressPercentage()

        // Butonları kampanya durumuna göre güncelle
        updateButtonState()
    }

    private fun updateButtonState() {
        // İşlemde olan kampanyalar için
        val isRunning = campaign.status == 0
        btnStart.isEnabled = !isRunning
        btnPause.isEnabled = isRunning
        btnStop.isEnabled = isRunning
    }

    private fun setupButtonListeners() {
        btnStart.setOnClickListener {
            startCampaign()
        }

        btnPause.setOnClickListener {
            pauseCampaign()
        }

        btnStop.setOnClickListener {
            stopCampaign()
        }
    }

    private fun startCampaign() {
        val request = CampaignActionRequest(campaign_id = campaign.id)

        RetrofitClient.apiService.startCampaign(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CampaignDetailActivity, "Kampanya başlatıldı", Toast.LENGTH_SHORT).show()
                        // Durumu güncelle
                        campaign = campaign.copy(status = 0)
                        updateButtonState()
                    } else {
                        Toast.makeText(this@CampaignDetailActivity, "Kampanya başlatılamadı", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@CampaignDetailActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun pauseCampaign() {
        val request = CampaignPauseRequest(
            campaign_id = campaign.id,
            duration = 0, // Sonsuz duraklat
            manual = true
        )

        RetrofitClient.apiService.pauseCampaign(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CampaignDetailActivity, "Kampanya duraklatıldı", Toast.LENGTH_SHORT).show()
                        // Butonları güncelle
                        btnStart.isEnabled = true
                        btnPause.isEnabled = false
                    } else {
                        Toast.makeText(this@CampaignDetailActivity, "Kampanya duraklatılamadı", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@CampaignDetailActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun stopCampaign() {
        val request = CampaignActionRequest(campaign_id = campaign.id)

        RetrofitClient.apiService.stopCampaign(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CampaignDetailActivity, "Kampanya durduruldu", Toast.LENGTH_SHORT).show()
                        // Durumu güncelle
                        campaign = campaign.copy(status = 3) // İptal edildi
                        displayCampaignDetails() // Tüm bilgileri yenile
                    } else {
                        Toast.makeText(this@CampaignDetailActivity, "Kampanya durdurulamadı", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@CampaignDetailActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}