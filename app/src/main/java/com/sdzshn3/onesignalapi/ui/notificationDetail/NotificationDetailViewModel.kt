package com.sdzshn3.onesignalapi.ui.notificationDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdzshn3.onesignalapi.model.Notification
import com.sdzshn3.onesignalapi.repository.MainRepository
import com.sdzshn3.onesignalapi.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class NotificationDetailViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _notification = MutableLiveData<Resource<Notification>>()
    val notification: LiveData<Resource<Notification>> get() = _notification

    fun load(url: String, appId: String, key: String) {
        _notification.value = Resource.loading(null, null)
        viewModelScope.launch {
            try {
                val response = repository.getNotification(url, appId, key)
                if (response.isSuccessful) {
                    _notification.value = Resource.success(response.body(), null)
                } else {
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val jsonArray = jsonObject.getJSONArray("errors")
                    val error = jsonArray[0] as String
                    _notification.value =
                        Resource.error(error, null, null)
                }
            } catch (e: Exception) {
                _notification.value = Resource.error("Something went wrong", null, null)
            }
        }

    }
}