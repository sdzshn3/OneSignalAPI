package com.sdzshn3.onesignalapi.ui.accountSetup

import androidx.lifecycle.ViewModel
import com.sdzshn3.onesignalapi.room.OneSignalApp
import com.sdzshn3.onesignalapi.room.OneSignalAppsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSetupViewModel @Inject constructor(
    private val oneSignalAppsDao: OneSignalAppsDao
) : ViewModel() {

    val appsList = oneSignalAppsDao.getAllLive()

    fun addApp(appName: String, appId: String, restApiKey: String) =
        CoroutineScope(Dispatchers.IO).launch {
            oneSignalAppsDao.insert(
                OneSignalApp(
                    appName = appName,
                    appId = appId,
                    restApiKey = restApiKey
                )
            )
        }
}