package com.sdzshn3.onesignalapi.repository

import com.sdzshn3.onesignalapi.di.OneSignal
import com.sdzshn3.onesignalapi.retrofit.ApiService
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(
    @OneSignal
    private val apiService: ApiService
) {
    suspend fun postNotification(
        requestBody: RequestBody,
        authorizationKey: String
    ) = apiService.postNotification(requestBody, authorizationKey)

    suspend fun getNotificationsHistory(
        appId: String,
        key: String
    ) = apiService.getNotificationsHistory(appId, key)

    suspend fun getNotification(
        url: String,
        appId: String,
        key: String
    ) = apiService.getNotification(url, appId, key)
}