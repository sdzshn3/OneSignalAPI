package com.sdzshn3.onesignalapi.oneSignalPOJO

import com.google.gson.annotations.SerializedName

data class ApnsAlert(@SerializedName("loc-key") var locKey: String? = null)