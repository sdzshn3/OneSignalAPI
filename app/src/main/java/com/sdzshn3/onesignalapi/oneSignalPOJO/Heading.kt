package com.sdzshn3.onesignalapi.oneSignalPOJO

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Heading(@SerializedName("en") var en: String? = null) : Parcelable