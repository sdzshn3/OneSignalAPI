package com.sdzshn3.onesignalapi.oneSignalPOJO

import com.google.gson.annotations.SerializedName

data class Filter (
    @SerializedName("field") var field: String? = null,
    @SerializedName("key") var key: String? = null,
    @SerializedName("relation") var relation: String? = null,
    @SerializedName("value") var value: String? = null,
    @SerializedName("hours_ago") var hoursAgo: String? = null,
    @SerializedName("radius") var radius: String? = null,
    @SerializedName("lat") var latitude: String? = null,
    @SerializedName("long") var longitude: String? = null,
    @SerializedName("operator") var operator: String? = null) {

}