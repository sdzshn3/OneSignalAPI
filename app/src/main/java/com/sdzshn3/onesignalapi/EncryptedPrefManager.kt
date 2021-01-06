package com.sdzshn3.onesignalapi

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

@SuppressLint("CommitPrefEdits")
class EncryptedPrefManager constructor(context: Context) {
    private val _oneSignalAppId: String = "oneSignalAppId"
    private val _restApiKey: String = "restApiKey"

    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefName = "oneSignalIds_v2"
        preferences = EncryptedSharedPreferences.create(
            context,
            prefName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editor = preferences.edit()
    }

    var oneSignalAppId: String?
        get() = preferences.getString(_oneSignalAppId, null)
        set(value) {
            editor.putString(this._oneSignalAppId, value)
            editor.commit()
        }

    var restApiKey: String?
        get() = preferences.getString(_restApiKey, null)
        set(value) {
            editor.putString(this._restApiKey, value)
            editor.commit()
        }
}