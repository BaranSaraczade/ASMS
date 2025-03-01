package com.example.asms.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.FrameLayout
import com.example.asms.R
import com.example.asms.ui.fragments.DevicesFragment
import com.example.asms.utils.PrefsManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import android.view.View
import com.example.asms.ui.fragments.CampaignsFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var usernameText: TextView
    private lateinit var emailText: TextView
    private lateinit var creditsText: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // View'ları bağla
        welcomeText = findViewById(R.id.welcomeText)
        usernameText = findViewById(R.id.usernameText)
        emailText = findViewById(R.id.emailText)
        creditsText = findViewById(R.id.creditsText)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Kullanıcı bilgilerini göster
        loadUserInfo()

        // Bottom Navigation'ı ayarla
        setupBottomNavigation()
    }

    private fun loadUserInfo() {
        PrefsManager.getUser()?.let { user ->
            welcomeText.text = "Hoş Geldiniz, ${user.username}"
            usernameText.text = "Kullanıcı Adı: ${user.username}"
            emailText.text = "E-posta: ${user.email}"
            creditsText.text = "Kredi: ${user.credits}"
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_home -> {
                    findViewById<FrameLayout>(R.id.fragment_container).visibility = View.GONE
                    findViewById<MaterialCardView>(R.id.userInfoCard).visibility = View.VISIBLE
                    true
                    true
                }
                R.id.navigation_devices -> {
                    findViewById<FrameLayout>(R.id.fragment_container).visibility = View.VISIBLE
                    findViewById<MaterialCardView>(R.id.userInfoCard).visibility = View.GONE
                    loadFragment(DevicesFragment())
                    true
                }
                R.id.navigation_sms -> {
                    // SMS sayfası
                    true
                }
                R.id.navigation_reports -> {
                    findViewById<FrameLayout>(R.id.fragment_container).visibility = View.VISIBLE
                    findViewById<MaterialCardView>(R.id.userInfoCard).visibility = View.VISIBLE
                    loadFragment(CampaignsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    // Profil sayfası
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}