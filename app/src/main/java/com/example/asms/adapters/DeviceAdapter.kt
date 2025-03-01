package com.example.asms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.asms.R
import com.example.asms.model.Device
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

class DeviceAdapter(
    private var devices: List<Device>,
    private val onDeviceClick: (Device) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceName: TextView = view.findViewById(R.id.deviceName)
        val phoneNumber: TextView = view.findViewById(R.id.phoneNumber)
        val deviceModel: TextView = view.findViewById(R.id.carrierName)
        val statusChip: Chip = view.findViewById(R.id.statusChip)
        val lastActive: TextView = view.findViewById(R.id.lastActive)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        holder.deviceName.text = device.device_name ?: "İsimsiz Cihaz"
        holder.phoneNumber.text = device.phone_number
        holder.deviceModel.text = device.device_model ?: "-"

        holder.statusChip.apply {
            text = if (device.status == 1) "Aktif" else "Pasif"
            setChipBackgroundColorResource(
                if (device.status == 1) R.color.status_active_bg else R.color.status_inactive_bg
            )
            setTextColor(context.getColor(
                if (device.status == 1) R.color.status_active_text else R.color.status_inactive_text
            ))
        }

        holder.lastActive.apply {
            if (device.last_active != null) {
                text = "Son aktivite: ${device.last_active}"
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            onDeviceClick(device)
        }
    }

    override fun getItemCount() = devices.size

    fun updateDevices(newDevices: List<Device>) {
        Log.d("DeviceAdapter", "Updating devices: ${newDevices.size}")
        devices = newDevices
        notifyDataSetChanged()
    }
}