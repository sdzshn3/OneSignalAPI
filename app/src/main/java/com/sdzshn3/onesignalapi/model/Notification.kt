package com.sdzshn3.onesignalapi.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sdzshn3.onesignalapi.oneSignalPOJO.Content
import com.sdzshn3.onesignalapi.oneSignalPOJO.Heading
import kotlinx.android.parcel.Parcelize

@Parcelize

data class Notification (
    @SerializedName("contents")
    val contents: Content,
    @SerializedName("id")
    val id: String,
    @SerializedName("successful")
    val successful: Int,
    @SerializedName("errored")
    val errored: Int,
    @SerializedName("failed")
    val failed: Int,
    @SerializedName("headings")
    val headings: Heading,
    @SerializedName("queued_at")
    val queuedAt: Long,
    @SerializedName("remaining")
    val remaining: Int,
    @SerializedName("platform_delivery_stats")
    val platformDeliveryStats: PlatformDeliveryStats
) : Parcelable