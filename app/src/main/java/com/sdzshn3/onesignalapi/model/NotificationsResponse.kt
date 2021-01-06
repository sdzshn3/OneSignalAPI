package com.sdzshn3.onesignalapi.model

import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("notifications")
    val notifications: List<Notification>
)