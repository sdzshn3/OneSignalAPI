@file:Suppress("unused")

package com.sdzshn3.onesignalapi

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    companion object {
        var appId: String? = null
        var restApiKey: String? = null
    }

    override fun onCreate() {
        super.onCreate()

        val options = FirebaseOptions.Builder()
            .setApplicationId(getString(R.string.google_app_id)) // Required for Analytics.
            .setProjectId(getString(R.string.project_id)) // Required for Firebase Installations.
            .setApiKey(getString(R.string.google_api_key)) // Required for Auth.
            .build()
        FirebaseApp.initializeApp(this, options, "OneSignal API")

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(getString(R.string.onesignal_app_id))

    }
}