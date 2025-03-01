package com.example.asms.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asms.R
import com.example.asms.adapters.DeviceAdapter
import com.example.asms.api.RetrofitClient
import com.example.asms.model.Device
import com.example.asms.model.DeviceRegistration
import com.example.asms.utils.PrefsManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DevicesFragment : Fragment() {

    private val PERMISSIONS_REQUEST_CODE = 123
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var scanButton: MaterialButton
    private val devices = mutableListOf<Device>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.deviceList)
        emptyView = view.findViewById(R.id.emptyView)
        scanButton = view.findViewById(R.id.scanButton)

        deviceAdapter = DeviceAdapter(devices) { device ->
            showDeviceOptions(device)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceAdapter
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }

        scanButton.setOnClickListener {
            checkPermissions()
        }

        // İlk yükleme
        loadDevices()
    }

    private fun loadDevices() {
        lifecycleScope.launch {
            try {
                val userId = PrefsManager.getUser()?.id
                if (userId == null) {
                    Log.e("DevicesFragment", "User ID is null")
                    return@launch
                }

                Log.d("DevicesFragment", "Loading devices for user: $userId")
                val response = RetrofitClient.apiService.getUserDevices(userId)

                Log.d("DevicesFragment", "Response: ${response.raw()}")

                if (response.isSuccessful) {
                    val deviceResponse = response.body()
                    if (deviceResponse?.success == true && deviceResponse.data != null) {
                        devices.clear()
                        devices.addAll(deviceResponse.data)

                        withContext(Dispatchers.Main) {
                            deviceAdapter.updateDevices(devices)
                            updateEmptyState()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, deviceResponse?.message ?: "Veri alınamadı", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DevicesFragment", "Error response: $errorBody")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Hata: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("DevicesFragment", "Error loading devices", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun scanDevices() {
        try {
            val subscriptionManager = requireContext().getSystemService(SubscriptionManager::class.java)

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList

                if (!activeSubscriptions.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        try {
                            var registeredNewDevice = false
                            activeSubscriptions.forEach { subscription ->
                                val deviceId = "${Build.DEVICE}_${subscription.iccId}_${subscription.simSlotIndex}"
                                val randomNumber = (1000..9999).random()
                                val deviceName = "${Build.MANUFACTURER} ${Build.MODEL} #$randomNumber"

                                val request = DeviceRegistration(
                                    user_id = PrefsManager.getUser()?.id ?: 0,
                                    device_id = deviceId,
                                    phone_number = subscription.number ?: "Unknown",
                                    device_name = deviceName,
                                    device_model = "${subscription.carrierName}"
                                )

                                val response = RetrofitClient.apiService.registerDevice(request)
                                if (response.isSuccessful) {
                                    registeredNewDevice = true
                                    loadDevices()  // Listeyi güncelle
                                }
                            }

                            if (!registeredNewDevice) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Tüm cihazlar zaten kayıtlı", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Cihaz kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Aktif SIM kart bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "SIM bilgileri alınamadı: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest,
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            scanDevices()
        }
    }

    private fun updateEmptyState() {
        if (devices.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                scanDevices()
            } else {
                Toast.makeText(
                    requireContext(),
                    "İzinler gerekli!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showDeviceOptions(device: Device) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cihaz İşlemleri")
            .setItems(arrayOf("SMS Gönder", "Cihazı Sil")) { _, which ->
                when (which) {
                    0 -> navigateToSendSms(device)
                    1 -> removeDevice(device)
                }
            }
            .show()
    }

    private fun navigateToSendSms(device: Device) {
        Toast.makeText(requireContext(), "SMS Gönder: ${device.phone_number}", Toast.LENGTH_SHORT).show()
    }

    private fun removeDevice(device: Device) {
        lifecycleScope.launch {
            try {
                devices.remove(device)
                deviceAdapter.updateDevices(devices)
                updateEmptyState()
                Toast.makeText(requireContext(), "Cihaz silindi", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Cihaz silinemedi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}