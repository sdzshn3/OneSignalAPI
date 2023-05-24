package com.sdzshn3.onesignalapi.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OneSignalApp(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "app_name")
    val appName: String,
    @ColumnInfo(name = "app_id")
    val appId: String,
    @ColumnInfo(name = "rest_api_key")
    val restApiKey: String
)
