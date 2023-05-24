package com.sdzshn3.onesignalapi

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("CommitPrefEdits")
class PrefManager constructor(private val context: Context) {
    private val _oneSignalAppId: String = "oneSignalAppId"
    private val _restApiKey: String = "restApiKey"
    private val _askedRating: String = "askedRating"

    private val prefName = "oneSignalIds"

    private val preferences: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()

    fun removeAllPrefs() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            context.deleteSharedPreferences(prefName)
        } else {
            editor.clear()
            editor.commit()
        }
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