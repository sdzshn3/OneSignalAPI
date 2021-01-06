package com.sdzshn3.onesignalapi.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlatformDeliveryStats(
    @SerializedName("android")
    val android: PlatformStats?,
    @SerializedName("ios")
    val ios: PlatformStats?,
    @SerializedName("chrome_web_push")
    val chromeWebPush: PlatformStats?,
    @SerializedName("safari_web_push")
    val safariWebPush: PlatformStats?,
    @SerializedName("firefox_web_push")
    val firefoxWebPush: PlatformStats?,
    @SerializedName("edge_web_push")
    val edgeWebPush: PlatformStats?
) : Parcelable