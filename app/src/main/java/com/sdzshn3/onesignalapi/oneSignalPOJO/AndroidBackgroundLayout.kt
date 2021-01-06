package com.sdzshn3.onesignalapi.oneSignalPOJO

import com.google.gson.annotations.SerializedName

data class AndroidBackgroundLayout (
    @SerializedName("image") var image: String? = null,
    @SerializedName("headings_color") var headingsColor: String? = null,
    @SerializedName("contents_color") var contentsColor: String? = null)