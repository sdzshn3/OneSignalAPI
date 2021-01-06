package com.sdzshn3.onesignalapi.ui.notificationHistory

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdzshn3.onesignalapi.EncryptedPrefManager
import com.sdzshn3.onesignalapi.model.NotificationsResponse
import com.sdzshn3.onesignalapi.repository.MainRepository
import com.sdzshn3.onesignalapi.utils.Resource
import kotlinx.coroutines.launch
import org.json.JSONObject

class NotificationHistoryViewModel
@ViewModelInject
constructor(
    private val repository: MainRepository,
    context: Application
) : ViewModel() {

    private val _allNotifications = MutableLiveData<Resource<NotificationsResponse>>()
    val allNotifications: LiveData<Resource<NotificationsResponse>> get() = _allNotifications

    init {
        val prefManager = EncryptedPrefManager(context)
        loadAllNotifications(
            prefManager.oneSignalAppId!!,
            "Basic ${prefManager.restApiKey}"
        )
    }

    fun loadAllNotifications(appId: String, key: String) {
        _allNotifications.value = Resource.loading(null, null)
        viewModelScope.launch {
            try {
                val response = repository.getNotificationsHistory(appId, key)
                if (response.isSuccessful) {
                    _allNotifications.value =
                        Resource.success(response.body()!!, null)
                } else {
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val jsonArray = jsonObject.getJSONArray("errors")
                    val error = jsonArray[0] as String
                    _allNotifications.value =
                        Resource.error(error, null, null)
                }
            } catch (e: Exception) {
                _allNotifications.value = Resource.error("Something went wrong", null, null)
            }

        }
    }
}