package com.sdzshn3.onesignalapi.oneSignalPOJO

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Content(@SerializedName("en") var en: String? = null) : Parcelable