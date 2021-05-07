package com.sdzshn3.onesignalapi.retrofit

import com.sdzshn3.onesignalapi.model.Notification
import com.sdzshn3.onesignalapi.model.NotificationsResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("notifications")
    @Headers("Content-Type: application/json; charset=utf-8")
    suspend fun postNotification(
        @Body body: RequestBody,
        @Header("Authorization") key: String
    ): Response<ResponseBody>

    @GET("notifications")
    suspend fun getNotificationsHistory(
        @Query("app_id") appId: String,
        @Header("Authorization") key: String
    ): Response<NotificationsResponse>

    @GET
    suspend fun getNotification(
        @Url url: String,
        @Query("app_id") appId: String,
        @Header("Authorization") key: String
    ): Response<Notification>
}