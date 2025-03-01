package com.example.asms.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asms.R
import com.example.asms.model.Campaign

class CampaignAdapter(
    private var campaigns: List<Campaign>,
    private val onCampaignClick: (Campaign) -> Unit
) : RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campaign, parent, false)
        return CampaignViewHolder(view)
    }

    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        val campaign = campaigns[position]
        holder.bind(campaign)
        holder.itemView.setOnClickListener { onCampaignClick(campaign) }
    }

    override fun getItemCount(): Int = campaigns.size

    fun updateCampaigns(newCampaigns: List<Campaign>) {
        campaigns = newCampaigns
        notifyDataSetChanged()
    }

    class CampaignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCampaignName: TextView = itemView.findViewById(R.id.tvCampaignName)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvDevice: TextView = itemView.findViewById(R.id.tvDevice)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val tvProgressText: TextView = itemView.findViewById(R.id.tvProgressText)

        fun bind(campaign: Campaign) {
            tvCampaignName.text = campaign.campaignName
            tvStatus.text = campaign.getStatusText()

            // Status badge'inin arka plan rengini ayarla
            val drawable = tvStatus.background as GradientDrawable
            drawable.setColor(Color.parseColor(campaign.getStatusColor()))

            tvDevice.text = campaign.deviceName
            tvDate.text = campaign.createdAt

            // İlerleme çubuğunu ayarla
            progressBar.progress = campaign.getProgressPercentage()
            tvProgressText.text = "${campaign.sentNumbers}/${campaign.totalNumbers} SMS"
        }
    }
}