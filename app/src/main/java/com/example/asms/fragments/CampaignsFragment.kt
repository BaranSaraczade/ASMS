package com.example.asms.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.asms.R
import com.example.asms.adapter.CampaignAdapter
import com.example.asms.api.RetrofitClient
import com.example.asms.model.Campaign
import com.example.asms.model.CampaignListResponse
import com.example.asms.utils.PrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var campaignAdapter: CampaignAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_campaigns, container, false)

        // View'ları bul
        recyclerView = view.findViewById(R.id.recyclerCampaigns)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        // User ID'yi SharedPreferences'dan al
        val userId = PrefsManager.getUser()?.id ?: 0

        // RecyclerView'ı ayarla
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        campaignAdapter = CampaignAdapter(emptyList()) { campaign ->
            // Kampanyaya tıklandığında
            openCampaignDetails(campaign)
        }
        recyclerView.adapter = campaignAdapter

        // SwipeRefresh listener'ı ayarla
        swipeRefresh.setOnRefreshListener {
            loadCampaigns(userId)
        }

        // Kampanyaları yükle
        loadCampaigns(userId)

        return view
    }

    private fun loadCampaigns(userId: Int) {
        progressBar.visibility = if (swipeRefresh.isRefreshing) View.GONE else View.VISIBLE

        RetrofitClient.apiService.getCampaigns(userId)
            .enqueue(object : Callback<CampaignListResponse> {
                override fun onResponse(
                    call: Call<CampaignListResponse>,
                    response: Response<CampaignListResponse>
                ) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false

                    if (response.isSuccessful) {
                        val campaigns = response.body()?.data ?: emptyList()
                        campaignAdapter.updateCampaigns(campaigns)

                        // Boş durum kontrolü
                        if (campaigns.isEmpty()) {
                            tvEmpty.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            tvEmpty.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    } else {
                        showError("Kampanyalar yüklenirken bir hata oluştu")
                    }
                }

                override fun onFailure(call: Call<CampaignListResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    showError("Bağlantı hatası: ${t.message}")
                }
            })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openCampaignDetails(campaign: Campaign) {
        // TODO: Bu kısımda CampaignDetailActivity'yi açacağız
        Toast.makeText(requireContext(), "Kampanya seçildi: ${campaign.campaignName}", Toast.LENGTH_SHORT).show()
    }
}