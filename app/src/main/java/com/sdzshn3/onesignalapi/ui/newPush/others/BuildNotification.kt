package com.sdzshn3.onesignalapi.ui.newPush.others

import android.app.Activity
import android.widget.Toast
import com.sdzshn3.onesignalapi.EncryptedPrefManager
import com.sdzshn3.onesignalapi.model.ActionButton
import com.sdzshn3.onesignalapi.databinding.FragmentNewPushBinding
import com.sdzshn3.onesignalapi.oneSignalPOJO.*
import com.sdzshn3.onesignalapi.ui.newPush.viewModel.NewPushViewModel
import java.util.*

fun buildNotification(
    binding: FragmentNewPushBinding,
    context: Activity,
    viewModel: NewPushViewModel,
    deliverAfterFinal: String,
    optimizedTimeZoneTime: String
): CreateNotification? {

    val prefManager = EncryptedPrefManager(context)
    val notification = CreateNotification()
    notification.appId = prefManager.oneSignalAppId

    if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.sendToAndroidSwitch.isChecked) {
        notification.isAndroid = true

        val selectedCategory =
            binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.categorySpinner.selectedItemPosition
        if (selectedCategory == 1 /* 1 is Created in App*/) {
            if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.existingChannelIdEditText.text.isNotBlank()) {
                notification.existingAndroidChannelId =
                    binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.existingChannelIdEditText.text.trim()
                        .toString()
            } else {
                Toast.makeText(
                    context,
                    "Please enter existing channel ID",
                    Toast.LENGTH_LONG
                ).show()
                return null
            }
        }

        if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidSoundEditText.text.trim()
                .isNotBlank()
        ) {
            notification.androidSound =
                binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidSoundEditText.text.trim()
                    .toString()
        }
        if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidLEDColorEditText.text.trim()
                .isNotBlank()
        ) {
            notification.androidLedColor =
                binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidLEDColorEditText.text.trim()
                    .toString()
        }
        when (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.lockScreenVisibilitySpinner.selectedItemPosition) {
            0 -> {
                //PUBLIC
                notification.androidLockScreenVisibility = 1
            }
            1 -> {
                //PRIVATE
                notification.androidLockScreenVisibility = 0
            }
            2 -> {
                //SECRET
                notification.androidLockScreenVisibility = -1
            }
        }
        if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.smallIconEditText.text.trim()
                .isNotBlank()
        ) {
            notification.smallIcon =
                binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.smallIconEditText.text.trim()
                    .toString()
        }
        if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidAccentColorEditText.text.trim()
                .isNotBlank()
        ) {
            notification.androidAccentColor =
                binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidAccentColorEditText.text.trim()
                    .toString()
        }
        if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.largeIconEditText.text.trim()
                .isNotBlank()
        ) {
            notification.largeIcon =
                binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.largeIconEditText.text.trim()
                    .toString()
        }
        if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidGroupKeyEditText.text.trim()
                .isNotBlank()
        ) {
            notification.androidGroup =
                binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidGroupKeyEditText.text.trim()
                    .toString()
        }
        if (binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidGroupMessageEditText.text.trim()
                .isNotBlank()
        ) {
            val androidGroupMessage = AndroidGroupMessage()
            androidGroupMessage.en =
                binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.androidGroupMessageEditText.text.trim()
                    .toString()
            notification.androidGroupMessage = androidGroupMessage
        }
    } else {
        notification.isAndroid = false
    }

    if (binding.messageLayout.platformSettingsLayout.platformSettingsOthers.sendToOthersSwitch.isChecked) {
        notification.isWpWns = true
        notification.isAdm = true
        notification.isChrome = true
        notification.isChromeWeb = true
        notification.isFirefox = true
        notification.isSafari = true
    } else {
        notification.isWpWns = false
        notification.isAdm = false
        notification.isChrome = false
        notification.isChromeWeb = false
        notification.isEmail = false
        notification.isFirefox = false
        notification.isSafari = false
    }

    if (binding.messageLayout.platformSettingsLayout.platformSettingsIos.sendToiOSSwitch.isChecked) {
        notification.isIos = true

        if (binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesRadioGroup.checkedRadioButtonId == binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeSetToRadio.id) {
            if (binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeSetToEditText.text.toString()
                    .isBlank()
            ) {
                Toast.makeText(
                    context,
                    "Please fill badge's \"set to\"",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return null
            }
            notification.iosBadgeType = "SetTo"
            notification.iosBadgeCount =
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeSetToEditText.text.toString()
                    .toInt()
        } else if (binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesRadioGroup.checkedRadioButtonId == binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeIncreaseByRadio.id) {
            if (binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesIncreaseByEditText.text.toString()
                    .isBlank()
            ) {
                Toast.makeText(
                    context,
                    "Please fill badge's \"increase by\"",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return null
            }
            notification.iosBadgeType = "Increase"
            notification.iosBadgeCount =
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesIncreaseByEditText.text.toString()
                    .toInt()
        }

        if (binding.messageLayout.platformSettingsLayout.platformSettingsIos.iosSoundEditText.text.toString()
                .isNotBlank()
        ) {
            notification.iosSound =
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.iosSoundEditText.text.toString()
                    .trim()
        }

        notification.contentAvailable =
            binding.messageLayout.platformSettingsLayout.platformSettingsIos.contentAvailableSwitch.isChecked

        if (binding.messageLayout.platformSettingsLayout.platformSettingsIos.iosCategoryEditText.text.toString()
                .isNotBlank()
        ) {
            notification.iosCategory =
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.iosCategoryEditText.text.toString()
                    .trim()
        }

        notification.mutableContent =
            binding.messageLayout.platformSettingsLayout.platformSettingsIos.mutableContentSwitch.isChecked

    } else {
        notification.isIos = false
    }


    if (binding.audienceLayout.audienceRadioGroup.checkedRadioButtonId == binding.audienceLayout.sendToSubUsersRadio.id) {
        notification.includedSegments = arrayOf("Subscribed Users")
    } else if (binding.audienceLayout.audienceRadioGroup.checkedRadioButtonId == binding.audienceLayout.sendToSegmentsRadio.id) {
        if (viewModel.includedSegments.isEmpty() && viewModel.excludedSegments.isEmpty()) {
            Toast.makeText(
                context,
                "Include or Exclude at least one Segment",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        if (viewModel.includedSegments.isNotEmpty()) {
            //val includedSegmentsArray = arrayOfNulls<String>(viewModel.includedSegments.size)
            //viewModel.includedSegments.toArray(includedSegmentsArray)
            notification.includedSegments = viewModel.includedSegments.toTypedArray()
        }

        if (viewModel.excludedSegments.isNotEmpty()) {
            //val excludedSegmentsArray = arrayOfNulls<String>(viewModel.excludedSegments.size)
            //viewModel.excludedSegments.toArray(excludedSegmentsArray)
            notification.excludedSegments = viewModel.excludedSegments.toTypedArray()
        }
    } else if (binding.audienceLayout.audienceRadioGroup.checkedRadioButtonId == binding.audienceLayout.sendUsingFiltersRadio.id) {
        if (viewModel.filters.isEmpty()) {
            Toast.makeText(
                context,
                "Please add at least one Filter",
                Toast.LENGTH_SHORT
            ).show()
            return null
        } else {
            val newFilters: ArrayList<Filter> = ArrayList()
            for (filter: Filter in viewModel.filters) {
                if (filter.operator.isNullOrBlank()) {
                    newFilters.add(filter)
                } else {
                    newFilters.add(Filter(operator = filter.operator))
                    filter.operator = null
                    newFilters.add(filter)
                }
            }
            notification.filters = newFilters
        }
    }

    if (binding.messageLayout.notificationTitleEditText.text.trim().isNotBlank()) {
        val heading =
            Heading(binding.messageLayout.notificationTitleEditText.text.trim().toString())
        notification.heading = heading
    }

    if (binding.messageLayout.notificationMessageEditText.text.trim().isNotBlank()) {
        val content =
            Content(binding.messageLayout.notificationMessageEditText.text.trim().toString())
        notification.content = content
    } else {
        Toast.makeText(
            context,
            "Notification Message is mandatory",
            Toast.LENGTH_LONG
        ).show()
        return null
    }

    if (binding.messageLayout.bigPictureUrlEditText.text.trim().isNotBlank()) {
        notification.bigPicture =
            binding.messageLayout.bigPictureUrlEditText.text.trim().toString()
    }

    if (binding.messageLayout.launchUrlEditText.text.trim().isNotBlank()) {
        notification.url = binding.messageLayout.launchUrlEditText.text.trim().toString()
    }

    if (binding.messageLayout.advancedSettingsLayout.collapseKeyEditText.text.trim()
            .isNotBlank()
    ) {
        notification.collapseId =
            binding.messageLayout.advancedSettingsLayout.collapseKeyEditText.text.trim()
                .toString()
    }

    if (binding.messageLayout.advancedSettingsLayout.priorityRadioGroup.checkedRadioButtonId == binding.messageLayout.advancedSettingsLayout.priorityHighRadio.id) {
        notification.priority = 10
    }

    if (binding.messageLayout.advancedSettingsLayout.timeToLiveEditText.text.trim()
            .isNotBlank()
    ) {
        notification.timeToLive =
            binding.messageLayout.advancedSettingsLayout.timeToLiveEditText.text.trim()
                .toString().toInt()
    }

    if (viewModel.actionButtons.isNotEmpty()) {
        val buttons: ArrayList<Button> =
            ArrayList()
        for (actionButton: ActionButton in viewModel.actionButtons) {
            buttons.add(
                Button(
                    actionButton.id,
                    actionButton.text,
                    actionButton.icon
                )
            )
        }

        notification.buttons = buttons
    }

    if (binding.scheduleLayout.deliveryRadioGroup.checkedRadioButtonId == binding.scheduleLayout.deliveryBeginAtParticularTimeRadio.id) {
        if (deliverAfterFinal.isNotBlank()) {
            notification.sendAfter = deliverAfterFinal
        } else {
            Toast.makeText(
                context,
                "Please choose delivery time",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
    }

    if (binding.scheduleLayout.perUserOptimisationRadioGroup.checkedRadioButtonId == binding.scheduleLayout.optimizationIntelligentRadio.id) {
        notification.delayedOptions = "last-active"
    }

    if (binding.scheduleLayout.perUserOptimisationRadioGroup.checkedRadioButtonId == binding.scheduleLayout.optimizationTimeZoneRadio.id) {
        if (optimizedTimeZoneTime.isNotBlank()) {
            notification.delayedOptions = "timezone"
            notification.deliveryTimeOfDay = optimizedTimeZoneTime
        } else {
            Toast.makeText(
                context,
                "Please choose user time zone time",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
    }

    return notification
}