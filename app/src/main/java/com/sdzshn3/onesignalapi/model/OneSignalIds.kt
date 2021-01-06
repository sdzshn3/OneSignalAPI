package com.sdzshn3.onesignalapi.model

import com.google.gson.annotations.SerializedName

data class OneSignalIds(
    @SerializedName("app_id")
    var appId: String? = null,
    @SerializedName("rest_api_key")
    var restApiKey: String? = null
)