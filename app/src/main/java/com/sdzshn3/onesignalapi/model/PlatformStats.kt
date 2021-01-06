package com.sdzshn3.onesignalapi.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlatformStats(
    @SerializedName("successful")
    val successful: Int,
    @SerializedName("failed")
    val failed: Int,
    @SerializedName("errored")
    val errored: Int
) : Parcelable