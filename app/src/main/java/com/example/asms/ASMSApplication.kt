package com.example.asms

import android.app.Application
import com.example.asms.utils.PrefsManager

class ASMSApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PrefsManager.init(this)
    }
}