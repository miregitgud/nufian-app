package com.example.nufianapp.main

import android.app.Application
import android.content.Context
import com.example.nufianapp.utils.LocaleHelper
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.setLocale(base!!, "en"))

        FirebaseFirestore.setLoggingEnabled(true)
    }
}