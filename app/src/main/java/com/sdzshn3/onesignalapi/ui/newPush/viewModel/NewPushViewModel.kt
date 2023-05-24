package com.sdzshn3.onesignalapi.ui.newPush.viewModel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.sdzshn3.onesignalapi.Event
import com.sdzshn3.onesignalapi.model.ActionButton
import com.sdzshn3.onesignalapi.model.Field
import com.sdzshn3.onesignalapi.model.Media
import com.sdzshn3.onesignalapi.repository.MainRepository
import com.sdzshn3.onesignalapi.oneSignalPOJO.Filter
import com.sdzshn3.onesignalapi.utils.NetworkHelper
import com.sdzshn3.onesignalapi.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.lang.Exception
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class NewPushViewModel @Inject constructor(
    applicationContext: Application,
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    var isPlatformSettingsExpanded: Boolean = false
    var isAdvancedSettingsExpanded: Boolean = false

    val deliveryAfterUi: MutableLiveData<String> = MutableLiveData()
    val deliveryAfterFinal: MutableLiveData<String> = MutableLiveData()

    val optimizedTimeZoneTime: MutableLiveData<String> = MutableLiveData()

    val fields: ArrayList<Field> = ArrayList()
    private val _fields: MutableLiveData<ArrayList<Field>> = MutableLiveData()
    val fieldsLiveData: LiveData<ArrayList<Field>> get() = _fields

    val medias: ArrayList<Media> = ArrayList()
    private val _medias: MutableLiveData<ArrayList<Media>> = MutableLiveData()
    val mediasLiveData: LiveData<ArrayList<Media>> get() = _medias

    val actionButtons: ArrayList<ActionButton> = ArrayList()
    private val _actionButtons: MutableLiveData<ArrayList<ActionButton>> = MutableLiveData()
    val actionButtonsLiveData: LiveData<ArrayList<ActionButton>> get() = _actionButtons

    val includedSegments: ArrayList<String> = ArrayList()
    private val _includedSegments: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val includedSegmentsLiveData: LiveData<ArrayList<String>> get() = _includedSegments

    val excludedSegments: ArrayList<String> = ArrayList()
    private val _excludedSegments: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val excludedSegmentsLiveData: LiveData<ArrayList<String>> get() = _excludedSegments

    val filters: ArrayList<Filter> = ArrayList()
    private val _filters: MutableLiveData<ArrayList<Filter>> = MutableLiveData()
    val filtersLiveData: LiveData<ArrayList<Filter>> get() = _filters

    val result = MutableLiveData<Event<Resource<String>>>()

    var idToNavigate = ""

    private var firebaseAnalytics: FirebaseAnalytics =
        FirebaseAnalytics.getInstance(applicationContext)

    fun addFilter(filter: Filter) {
        filters.add(filter)
        _filters.value = filters
    }

    fun deleteFilter(removeAt: Int) {
        filters.removeAt(removeAt)
        _filters.value = filters
    }

    fun excludeSegment(segment: String) {
        excludedSegments.add(segment)
        _excludedSegments.value = excludedSegments
    }

    fun deleteExcludedSegment(removeAt: Int) {
        excludedSegments.removeAt(removeAt)
        _excludedSegments.value = excludedSegments
    }

    fun includeSegment(segment: String) {
        includedSegments.add(segment)
        _includedSegments.value = includedSegments
    }

    fun deleteIncludedSegment(removeAt: Int) {
        includedSegments.removeAt(removeAt)
        _includedSegments.value = includedSegments
    }

    fun addActionButton(actionId: String, text: String, icon: String) {
        actionButtons.add(
            ActionButton(actionId, text, icon)
        )
        _actionButtons.value = actionButtons
    }

    fun deleteActionButton(removeAt: Int) {
        actionButtons.removeAt(removeAt)
        val actionButtonList: ArrayList<ActionButton> = ArrayList()

        for (i in 0 until actionButtons.size) {
            val actionId: String? = actionButtons[i].id
            val text: String? = actionButtons[i].text
            val icon: String? = actionButtons[i].icon
            actionButtonList.add(
                ActionButton(
                    actionId,
                    text,
                    icon
                )
            )
        }

        actionButtons.clear()
        actionButtons.addAll(actionButtonList)
        _actionButtons.value = actionButtons
    }

    fun addIosMedia(key: String, value: String) {
        medias.add(Media(medias.size + 1, key, value))
        _medias.value = medias
    }

    fun deleteIosMedia(removeAt: Int) {
        medias.removeAt(removeAt)
        val mediaList: ArrayList<Media> = ArrayList()

        for (i in 0 until medias.size) {
            val key: String? = medias[i].key
            val value: String? = medias[i].value
            mediaList.add(
                Media(
                    i + 1,
                    key,
                    value
                )
            )
        }

        medias.clear()
        medias.addAll(mediaList)
        _medias.value = medias
    }

    fun addField(count: Int, key: String, value: String) {
        fields.add(Field(number = count, key = key, value = value))
        _fields.value = fields
    }

    fun deleteField(removeAt: Int) {
        fields.removeAt(removeAt)
        val fieldList: ArrayList<Field> = ArrayList()
        for (i in 0 until fields.size) {
            val key: String? = fields[i].key
            val value: String? = fields[i].value
            fieldList.add(
                Field(
                    i + 1,
                    key,
                    value
                )
            )
        }

        fields.clear()
        fields.addAll(fieldList)
        _fields.value = fields
    }

    fun postNotification(
        requestBody: RequestBody,
        authorizationKey: String,
        isNotificationPreview: Boolean
    ) {
        result.value = Event(Resource.loading(null, isNotificationPreview))
        if (!networkHelper.isNetworkConnected()) {
            result.value =
                Event(Resource.error("Check internet connection", null, isNotificationPreview))
            return
        }
        viewModelScope.launch {
            val response: Response<ResponseBody>
            try {
                response = mainRepository.postNotification(requestBody, authorizationKey)
            } catch (e: Exception) {
                result.postValue(
                    Event(
                        Resource.error(
                            "Something went wrong",
                            null,
                            isNotificationPreview
                        )
                    )
                )
                return@launch
            }

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    var errorMessage = ""
                    val body = response.body()
                    val rootObject: JSONObject
                    try {
                        rootObject = JSONObject(body!!.string())
                    } catch (e: JSONException) {
                        errorMessage = "Server might be down. Please try again."
                        result.postValue(
                            Event(
                                Resource.error(
                                    errorMessage,
                                    null,
                                    isNotificationPreview
                                )
                            )
                        )
                        val params = Bundle()
                        params.putString("success", "false")
                        params.putString("desc", errorMessage)
                        firebaseAnalytics
                            .logEvent("notification", params)
                        return@withContext
                    }


                    try {
                        val errors = rootObject.getJSONArray("errors")
                        for (i in 0 until errors.length()) {
                            errorMessage += errors[i]
                            errorMessage += "."
                        }
                        val params = Bundle()
                        params.putString("success", "false")
                        params.putString("desc", errorMessage)
                        firebaseAnalytics
                            .logEvent("notification", params)
                        result.postValue(
                            Event(
                                Resource.error(
                                    errorMessage,
                                    null,
                                    isNotificationPreview
                                )
                            )
                        )

                    } catch (e: JSONException) {
                        val id = rootObject.optString("id")
                        //val recipients = rootObject.optInt("recipients")
                        val data = "Notification ID: $id"

                        val params = Bundle()
                        params.putString("success", "true")
                        params.putString("desc", data)
                        firebaseAnalytics
                            .logEvent("notification", params)

                        result.postValue(Event(Resource.success(data, isNotificationPreview)))
                    }
                } else {
                    var errorMessage = ""
                    val rootObject = JSONObject(response.errorBody()!!.string())
                    val errors = rootObject.getJSONArray("errors")
                    for (i in 0 until errors.length()) {
                        errorMessage += errors[i]
                        errorMessage += "."
                    }

                    val params = Bundle()
                    params.putString("success", "false")
                    params.putString("desc", errorMessage)
                    firebaseAnalytics
                        .logEvent("notification", params)
                    result.postValue(
                        Event(
                            Resource.error(
                                errorMessage,
                                null,
                                isNotificationPreview
                            )
                        )
                    )
                }
            }
        }
    }
}