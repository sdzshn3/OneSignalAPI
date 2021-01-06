package com.sdzshn3.onesignalapi.oneSignalPOJO

import com.google.gson.annotations.SerializedName

data class Button(
    @SerializedName("id") var id: String? = null,
    @SerializedName("text") var text: String? = null,
    @SerializedName( "icon") var icon: String? = null)