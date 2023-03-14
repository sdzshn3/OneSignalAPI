package com.sdzshn3.onesignalapi.ui.newPush.view

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sdzshn3.onesignalapi.*
import com.sdzshn3.onesignalapi.databinding.*
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import com.sdzshn3.onesignalapi.model.Field
import com.sdzshn3.onesignalapi.model.Media
import com.sdzshn3.onesignalapi.model.OneSignalIds
import com.sdzshn3.onesignalapi.oneSignalPOJO.CreateNotification
import com.sdzshn3.onesignalapi.oneSignalPOJO.Filter
import com.sdzshn3.onesignalapi.ui.AccountSetupActivity
import com.sdzshn3.onesignalapi.ui.newPush.adapters.*
import com.sdzshn3.onesignalapi.ui.newPush.others.NotificationResultDialog
import com.sdzshn3.onesignalapi.ui.newPush.others.buildNotification
import com.sdzshn3.onesignalapi.ui.newPush.others.manage
import com.sdzshn3.onesignalapi.ui.newPush.others.verify
import com.sdzshn3.onesignalapi.ui.newPush.viewModel.NewPushViewModel
import com.sdzshn3.onesignalapi.utils.NetworkHelper
import com.sdzshn3.onesignalapi.utils.Resource
import com.sdzshn3.onesignalapi.utils.Status
import com.sdzshn3.onesignalapi.utils.resizeBitmapForLargeIconArea
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.math.BigInteger
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

const val NOTIFICATION_RESULT_DIALOG_ERROR = "NOTIFICATION_RESULT_DIALOG_ERROR"
const val NOTIFICATION_RESULT_DIALOG_SUCCESS = "NOTIFICATION_RESULT_DIALOG_SUCCESS"

@Suppress("LABEL_NAME_CLASH", "BlockingMethodInNonBlockingContext")
@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class NewPushFragment : Fragment(R.layout.fragment_new_push), View.OnClickListener {

    private lateinit var additionalDataAdapter: AdditionalDataAdapter
    private lateinit var actionButtonsAdapter: ActionButtonsAdapter
    private lateinit var includedSegmentsAdapter: SegmentsAdapter
    private lateinit var excludedSegmentsAdapter: SegmentsAdapter
    private lateinit var filtersAdapter: FiltersAdapter
    private lateinit var mediaAdapter: MediaAdapter

    private lateinit var prefManager: EncryptedPrefManager

    private var deliverAfterFinal = ""
    private var optimizedTimeZoneTime = ""

    private val viewModel: NewPushViewModel by viewModels()

    @Inject
    lateinit var networkHelper: NetworkHelper

    private lateinit var binding: FragmentNewPushBinding

    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: ReviewInfo? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                processNotification()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Snackbar.make(
                    binding.root,
                    "Notification permission is needed to show the preview of notification",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            val notificationResultDialog =
                parentFragmentManager.findFragmentByTag(NOTIFICATION_RESULT_DIALOG_SUCCESS) as NotificationResultDialog?
            notificationResultDialog?.let {
                it.setMoreDetailsListener {
                    val id = viewModel.idToNavigate.split("ID: ")[1]
                    val direction =
                        NewPushFragmentDirections.actionNewPushFragmentToNotificationDetailFragment(
                            null,
                            id
                        )
                    findNavController().navigate(direction)
                }
                it.setOnDialogClosedListener {
                    reviewInfo?.let { info ->
                        reviewManager.launchReviewFlow(requireActivity(), info)
                            .addOnSuccessListener {
                                log("review Ask success")
                            }.addOnFailureListener { exception ->
                                log("review ask failed: ${exception.message}")
                            }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = FragmentNewPushBinding.bind(view)

        val firebaseAuth = FirebaseAuth.getInstance()
        if (Application.appId.isNullOrBlank()) {
            if (firebaseAuth.currentUser == null) {
                firebaseAuth.signInAnonymously().addOnSuccessListener {
                    FirebaseFirestore.getInstance().collection("IdsAndKeys").document("OneSignal")
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val oneSignalIds = documentSnapshot.toObject(OneSignalIds::class.java)
                            oneSignalIds?.let {
                                Application.appId = it.appId
                                Application.restApiKey = it.restApiKey
                            }
                        }
                }
            } else {
                FirebaseFirestore.getInstance().collection("IdsAndKeys").document("OneSignal").get()
                    .addOnSuccessListener { documentSnapshot ->
                        val oneSignalIds = documentSnapshot.toObject(OneSignalIds::class.java)
                        oneSignalIds?.let {
                            Application.appId = it.appId
                            Application.restApiKey = it.restApiKey
                        }
                    }
            }
        }

        binding.messageLayout.root.setOnClickListener(this)

        prefManager = EncryptedPrefManager(requireContext())
        reviewManager = ReviewManagerFactory.create(requireContext())
        reviewManager.requestReviewFlow().addOnSuccessListener {
            log(it.toString())
            reviewInfo = it
        }
        if (viewModel.isAdvancedSettingsExpanded) {
            binding.messageLayout.arrow2.animate().rotation(90f)
            binding.messageLayout.advancedSettingsLayout.root.visibility = VISIBLE
        } else {
            binding.messageLayout.arrow2.animate().rotation(0f)
            binding.messageLayout.advancedSettingsLayout.root.visibility = GONE
        }

        if (viewModel.isPlatformSettingsExpanded) {
            binding.messageLayout.arrow1.animate().rotation(90f)
            binding.messageLayout.platformSettingsLayout.root.visibility = VISIBLE
        } else {
            binding.messageLayout.arrow1.animate().rotation(0f)
            binding.messageLayout.platformSettingsLayout.root.visibility = GONE
        }

        // Settings default visibilities
        //binding.messageLayout.platformSettingsLayout.root.visibility = GONE
        //binding.messageLayout.advancedSettingsLayout.root.visibility = GONE
        binding.audienceLayout.segmentsLayout.segmentsLinearLayout.visibility = GONE
        binding.scheduleLayout.optimizeByUserTimeZoneLinearLayout.visibility = GONE
        binding.scheduleLayout.deliveryBeginAtParticularTimeLinearLayout.visibility = GONE
        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.localChannelIdLayout.visibility =
            GONE
        binding.audienceLayout.filtersLayout.filtersLinearLayout.visibility = GONE
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeSetToEditText.visibility =
            GONE
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesIncreaseByEditText.visibility =
            GONE

        setupRecyclerViewsAndAdapters()
        observerViewModel()
        setLayoutAnimations()

        val categoryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_list, android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.categorySpinner.adapter =
            categoryAdapter

        val lockScreenVisibilityAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lock_screen_visibility_list, android.R.layout.simple_spinner_item
        )
        lockScreenVisibilityAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.lockScreenVisibilitySpinner.adapter =
            lockScreenVisibilityAdapter

        binding.messageLayout.root.setOnClickListener {
            removeFocusAndHideKeyboard()
        }
        binding.audienceLayout.root.setOnClickListener {
            removeFocusAndHideKeyboard()
        }
        binding.scheduleLayout.root.setOnClickListener {
            removeFocusAndHideKeyboard()
        }

        binding.messageLayout.platformSettingsExtender.setOnClickListener(this)
        binding.messageLayout.advancedSettingsExtender.setOnClickListener(this)


        excludedSegmentsAdapter.setDeleteButtonListener(object : DeleteButtonListener {
            override fun onDeleteButtonPressed(position: Int) {
                viewModel.deleteExcludedSegment(position)
            }
        })
        includedSegmentsAdapter.setDeleteButtonListener(object : DeleteButtonListener {
            override fun onDeleteButtonPressed(position: Int) {
                viewModel.deleteIncludedSegment(position)
            }
        })
        mediaAdapter.setDeleteButtonListener(object : DeleteButtonListener {
            override fun onDeleteButtonPressed(position: Int) {
                viewModel.deleteIosMedia(position)
            }
        })
        additionalDataAdapter.setDeleteButtonListener(object : DeleteButtonListener {
            override fun onDeleteButtonPressed(position: Int) {
                viewModel.deleteField(removeAt = position)
            }
        })
        actionButtonsAdapter.setDeleteButtonListener(object : DeleteButtonListener {
            override fun onDeleteButtonPressed(position: Int) {
                viewModel.deleteActionButton(position)
            }
        })
        filtersAdapter.setDeleteButtonListener(object : DeleteButtonListener {
            override fun onDeleteButtonPressed(position: Int) {
                viewModel.deleteFilter(position)
            }
        })

        binding.audienceLayout.filtersLayout.addFilterButton.setOnClickListener(this)
        binding.audienceLayout.segmentsLayout.addAnotherExcludeSegmentButton.setOnClickListener(this)
        binding.audienceLayout.segmentsLayout.addAnotherSegmentButton.setOnClickListener(this)
        binding.messageLayout.advancedSettingsLayout.addAnotherActionButton.setOnClickListener(this)
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.addIosMediaButton.setOnClickListener(
            this
        )
        //binding.messageLayout.bigPictureUrlEditText.click

        // Platform selection switches
        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.sendToAndroidSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.sendToAndroidContentLinearLayout.visibility =
                        VISIBLE
                } else {
                    if (!binding.messageLayout.platformSettingsLayout.platformSettingsIos.sendToiOSSwitch.isChecked &&
                        !binding.messageLayout.platformSettingsLayout.platformSettingsOthers.sendToOthersSwitch.isChecked
                    ) {
                        Toast.makeText(
                            requireActivity(),
                            "You must enable at least one platform",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.sendToAndroidSwitch.isChecked =
                            true
                        return@setOnCheckedChangeListener
                    }
                    binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.sendToAndroidContentLinearLayout.visibility =
                        GONE
                }
            }
        }
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.sendToiOSSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.messageLayout.platformSettingsLayout.platformSettingsIos.sendToiOSContentLinearLayout.visibility =
                        VISIBLE
                } else {
                    if (!binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.sendToAndroidSwitch.isChecked &&
                        !binding.messageLayout.platformSettingsLayout.platformSettingsOthers.sendToOthersSwitch.isChecked
                    ) {
                        Toast.makeText(
                            requireActivity(),
                            "You must enable at least one platform",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.messageLayout.platformSettingsLayout.platformSettingsIos.sendToiOSSwitch.isChecked =
                            true
                        return@setOnCheckedChangeListener
                    }
                    binding.messageLayout.platformSettingsLayout.platformSettingsIos.sendToiOSContentLinearLayout.visibility =
                        GONE
                }
            }
        }
        binding.messageLayout.platformSettingsLayout.platformSettingsOthers.sendToOthersSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (!isChecked) {
                    if (!binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.sendToAndroidSwitch.isChecked &&
                        !binding.messageLayout.platformSettingsLayout.platformSettingsIos.sendToiOSSwitch.isChecked
                    ) {
                        Toast.makeText(
                            requireActivity(),
                            "You must enable at least one platform",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.messageLayout.platformSettingsLayout.platformSettingsOthers.sendToOthersSwitch.isChecked =
                            true
                    }
                }
            }
        }

        // Audience Target Radios
        binding.audienceLayout.sendToSegmentsRadio.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.audienceLayout.segmentsLayout.segmentsLinearLayout.visibility =
                        VISIBLE
                } else {
                    binding.audienceLayout.segmentsLayout.segmentsLinearLayout.visibility =
                        GONE
                }
            }
        }
        binding.audienceLayout.sendUsingFiltersRadio.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.audienceLayout.filtersLayout.filtersLinearLayout.visibility =
                        VISIBLE
                } else {
                    binding.audienceLayout.filtersLayout.filtersLinearLayout.visibility = GONE
                }
            }
        }

        // Schedules
        binding.scheduleLayout.optimizationTimeZoneRadio.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.scheduleLayout.optimizeByUserTimeZoneLinearLayout.visibility =
                        VISIBLE
                } else {
                    binding.scheduleLayout.optimizeByUserTimeZoneLinearLayout.visibility = GONE
                }
            }
        }


        binding.scheduleLayout.deliveryBeginAtParticularTimeRadio.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.scheduleLayout.deliveryBeginAtParticularTimeLinearLayout.visibility =
                        VISIBLE
                } else {
                    binding.scheduleLayout.deliveryBeginAtParticularTimeLinearLayout.visibility =
                        GONE
                }
            }
        }

        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1) {
                        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.localChannelIdLayout.visibility =
                            VISIBLE
                    } else {
                        binding.messageLayout.platformSettingsLayout.platformSettingsAndroid.localChannelIdLayout.visibility =
                            GONE
                    }
                }
            }

        // iOS Badges radios
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeSetToRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeSetToEditText.visibility =
                    VISIBLE
            } else {
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeSetToEditText.visibility =
                    GONE
            }
        }

        binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgeIncreaseByRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesIncreaseByEditText.visibility =
                    VISIBLE
            } else {
                binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesIncreaseByEditText.visibility =
                    GONE
            }
        }

        binding.messageLayout.notificationPreviewButton.setOnClickListener(this)
        binding.messageLayout.notificationPreviewButton.setOnLongClickListener {
            showJson()
            return@setOnLongClickListener true
        }
        binding.scheduleLayout.chooseDateAndTimeButton.setOnClickListener(this)
        binding.messageLayout.advancedSettingsLayout.addAnotherFieldButton.setOnClickListener(this)
        binding.scheduleLayout.chooseTimeButton.setOnClickListener(this)
        binding.sendFAB.setOnClickListener(this)
    }

    private fun setupRecyclerViewsAndAdapters() {
        additionalDataAdapter = AdditionalDataAdapter(requireContext(), arrayListOf())
        actionButtonsAdapter = ActionButtonsAdapter(requireContext(), arrayListOf())
        includedSegmentsAdapter = SegmentsAdapter(requireContext(), ArrayList())
        excludedSegmentsAdapter = SegmentsAdapter(requireContext(), arrayListOf())
        filtersAdapter = FiltersAdapter(requireContext())
        mediaAdapter = MediaAdapter(requireContext(), arrayListOf())

        binding.messageLayout.advancedSettingsLayout.additionalDataRecyclerView.setHasFixedSize(
            false
        )
        binding.messageLayout.advancedSettingsLayout.actionButtonsRecyclerView.setHasFixedSize(false)
        binding.audienceLayout.segmentsLayout.includedSegmentsRecyclerView.setHasFixedSize(false)
        binding.audienceLayout.segmentsLayout.excludedSegmentsRecyclerView.setHasFixedSize(false)
        binding.audienceLayout.filtersLayout.filtersRecyclerView.setHasFixedSize(false)
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.iosMediaRecyclerView.setHasFixedSize(
            false
        )

        binding.messageLayout.advancedSettingsLayout.additionalDataRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.messageLayout.advancedSettingsLayout.actionButtonsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.audienceLayout.segmentsLayout.includedSegmentsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.audienceLayout.segmentsLayout.excludedSegmentsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.audienceLayout.filtersLayout.filtersRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.iosMediaRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.messageLayout.advancedSettingsLayout.additionalDataRecyclerView.adapter =
            additionalDataAdapter
        binding.messageLayout.advancedSettingsLayout.actionButtonsRecyclerView.adapter =
            actionButtonsAdapter
        binding.audienceLayout.segmentsLayout.includedSegmentsRecyclerView.adapter =
            includedSegmentsAdapter
        binding.audienceLayout.segmentsLayout.excludedSegmentsRecyclerView.adapter =
            excludedSegmentsAdapter
        binding.audienceLayout.filtersLayout.filtersRecyclerView.adapter = filtersAdapter
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.iosMediaRecyclerView.adapter =
            mediaAdapter
    }

    private fun setLayoutAnimations() {
        binding.messageLayout.messageSectionLinearLayout.layoutTransition.enableTransitionType(
            LayoutTransition.CHANGING
        )
        binding.audienceLayout.audienceSectionLinearLayout.layoutTransition.enableTransitionType(
            LayoutTransition.CHANGING
        )
        binding.messageLayout.platformSettingsLayout.platformSettingsLayout.layoutTransition.enableTransitionType(
            LayoutTransition.CHANGING
        )
        binding.messageLayout.platformSettingsLayout.platformSettingsIos.badgesRadioGroup.layoutTransition.enableTransitionType(
            LayoutTransition.CHANGING
        )
    }

    private fun observerViewModel() {
        viewModel.optimizedTimeZoneTime.observe(viewLifecycleOwner) {
            optimizedTimeZoneTime = it
            binding.scheduleLayout.deliversAtInTheirTimeZoneTextView.text =
                "Delivers at $optimizedTimeZoneTime in their local timezone"
        }

        viewModel.deliveryAfterFinal.observe(viewLifecycleOwner) {
            deliverAfterFinal = it
        }

        viewModel.deliveryAfterUi.observe(viewLifecycleOwner) {
            binding.scheduleLayout.selectedDateAndTimeTextView.text = "Delivers at $it"
        }

        viewModel.fieldsLiveData.observe(requireActivity()) {
            additionalDataAdapter.submitList(it)
        }

        viewModel.mediasLiveData.observe(requireActivity()) {
            mediaAdapter.submitList(it)
        }

        viewModel.actionButtonsLiveData.observe(requireActivity()) {
            actionButtonsAdapter.submitList(it)
        }

        viewModel.includedSegmentsLiveData.observe(requireActivity()) {
            includedSegmentsAdapter.submitList(it)
        }

        viewModel.excludedSegmentsLiveData.observe(requireActivity()) {
            excludedSegmentsAdapter.submitList(it)
        }

        viewModel.filtersLiveData.observe(requireActivity()) {
            filtersAdapter.submitList(it)
        }

        viewModel.result.observe(viewLifecycleOwner) {
            it.peekContent().let { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                        if (resource.isNotificationPreview!!) {
                            binding.messageLayout.notificationPreviewButton.isEnabled = false
                            binding.messageLayout.notificationPreviewButton.startAnimation()
                        } else {
                            binding.sendFAB.isEnabled = false
                            binding.fabProgressCircle.visibility = VISIBLE
                        }
                    }
                    else -> {
                    }
                }
            }
            it.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.ERROR -> {
                        if (resource.isNotificationPreview!!) {
                            binding.messageLayout.notificationPreviewButton.isEnabled = true
                            binding.messageLayout.notificationPreviewButton.revertAnimation()
                            Toast.makeText(
                                requireActivity(),
                                "Failed to show preview notification",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            binding.sendFAB.isEnabled = true
                            binding.fabProgressCircle.visibility = GONE
                            NotificationResultDialog.newInstance(
                                resource.message!!,
                                "Unsuccessful",
                                false
                            ).show(parentFragmentManager, NOTIFICATION_RESULT_DIALOG_ERROR)
                        }
                    }
                    Status.SUCCESS -> {
                        if (resource.isNotificationPreview!!) {
                            binding.messageLayout.notificationPreviewButton.isEnabled = true
                            binding.messageLayout.notificationPreviewButton.revertAnimation()
                            Toast.makeText(
                                requireActivity(),
                                "Notification preview has been sent",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.idToNavigate = resource.data!!.split("ID: ")[1]
                            binding.sendFAB.isEnabled = true
                            binding.fabProgressCircle.visibility = GONE
                            NotificationResultDialog.newInstance(
                                message = resource.data,
                                title = "Successful",
                                success = true
                            ).apply {
                                setMoreDetailsListener {
                                    val id = resource.data.split("ID: ")[1]
                                    val direction =
                                        NewPushFragmentDirections.actionNewPushFragmentToNotificationDetailFragment(
                                            null,
                                            id
                                        )
                                    findNavController().navigate(direction)
                                }
                                setOnDialogClosedListener {
                                    reviewInfo?.let { info ->
                                        reviewManager.launchReviewFlow(requireActivity(), info)
                                            .addOnSuccessListener {
                                                log("review Ask success")
                                            }.addOnFailureListener { exception ->
                                                log("review ask failed: ${exception.message}")
                                            }
                                    }
                                }
                            }.show(parentFragmentManager, NOTIFICATION_RESULT_DIALOG_SUCCESS)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_activity_menu, menu)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_app_menu_item) {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm !")
                .setMessage("This will clear current app's ID and REST API Key. Are you sure you want to continue?")
                .setPositiveButton("YES") { _: DialogInterface, _: Int ->
                    prefManager.restApiKey = null
                    prefManager.oneSignalAppId = null

                    startActivity(Intent(requireContext(), AccountSetupActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }.create()
            dialog.show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                    resources.getColor(
                        R.color.otherTextColor,
                        requireActivity().theme
                    )
                )
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                    resources.getColor(
                        R.color.otherTextColor,
                        requireActivity().theme
                    )
                )
            } else {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.otherTextColor))
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.otherTextColor))
            }

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeFocusAndHideKeyboard() {
        val viewInFocus = requireActivity().currentFocus
        if (viewInFocus is EditText) {
            viewInFocus.clearFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(viewInFocus.windowToken, 0)
        }
    }

    private fun showJson() {
        val notification = buildNotification(
            binding,
            requireActivity(),
            viewModel,
            deliverAfterFinal,
            optimizedTimeZoneTime
        ) ?: return

        val json = Gson().toJson(notification)
        val jsonObject = JSONObject(json)

        if (viewModel.fields.isNotEmpty()) {
            val stringMap = HashMap<String?, String?>()
            for (field: Field in viewModel.fields) {
                @Suppress("ReplacePutWithAssignment")
                stringMap.put(field.key, field.value)
            }
            jsonObject.put("data", JSONObject(stringMap as Map<*, *>))
        }

        if (viewModel.medias.isNotEmpty()) {
            val stringMap = HashMap<String?, String?>()
            for (media: Media in viewModel.medias) {
                @Suppress("ReplacePutWithAssignment")
                stringMap.put(media.key, media.value)
            }
            jsonObject.put("ios_attachments", JSONObject(stringMap as Map<*, *>))
        }

        val direction = NewPushFragmentDirections.actionNewPushFragmentToJsonPreviewFragment(
            jsonObject.toString(5)
        )
        findNavController().navigate(direction)

    }

    override fun onClick(view: View?) {
        removeFocusAndHideKeyboard()
        when (view) {
            binding.sendFAB -> {
                val notification = buildNotification(
                    binding,
                    requireActivity(),
                    viewModel,
                    deliverAfterFinal,
                    optimizedTimeZoneTime
                ) ?: return

                val json = Gson().toJson(notification)
                val jsonObject = JSONObject(json)

                if (viewModel.fields.isNotEmpty()) {
                    val stringMap = HashMap<String?, String?>()
                    for (field: Field in viewModel.fields) {
                        @Suppress("ReplacePutWithAssignment")
                        stringMap.put(field.key, field.value)
                    }
                    jsonObject.put("data", JSONObject(stringMap as Map<*, *>))
                }

                if (viewModel.medias.isNotEmpty()) {
                    val stringMap = HashMap<String?, String?>()
                    for (media: Media in viewModel.medias) {
                        @Suppress("ReplacePutWithAssignment")
                        stringMap.put(media.key, media.value)
                    }
                    jsonObject.put("ios_attachments", JSONObject(stringMap as Map<*, *>))
                }

                val requestBody =
                    jsonObject.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                viewModel.postNotification(
                    requestBody,
                    "Basic ${prefManager.restApiKey}",
                    false
                )
            }

            binding.messageLayout.notificationPreviewButton -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when {
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            // You can use the API that requires the permission.
                            processNotification()
                        }
                        shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                            // In an educational UI, explain to the user why your app requires this
                            // permission for a specific feature to behave as expected, and what
                            // features are disabled if it's declined. In this UI, include a
                            // "cancel" or "no thanks" button that lets the user continue
                            // using your app without granting the permission.
                            Snackbar.make(
                                binding.root,
                                "Notification permission is needed to show the preview of notification",
                                Snackbar.LENGTH_LONG
                            ).setAction("grant") {
                                requestPermissionLauncher.launch(
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                )
                            }.show()

                        }
                        else -> {
                            // You can directly ask for the permission.
                            // The registered ActivityResultCallback gets the result of this request.

                            requestPermissionLauncher.launch(
                                android.Manifest.permission.POST_NOTIFICATIONS
                            )

                        }
                    }
                } else {
                    processNotification()
                }

            }

            binding.scheduleLayout.chooseDateAndTimeButton -> {
                val datePicker = DatePickerFragment()
                datePicker.setDateSetListener(object :
                    DatePickerFragment.DateSetListener {
                    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {

                        val timePicker =
                            TimePickerFragment()
                        timePicker.setTimeSetListener(object :
                            TimePickerFragment.TimeSetListener {
                            override fun onTimeSet(hourOfDay: Int, minute: Int) {
                                //Sample format
                                //"2015-09-24 14:00:00 GMT-0700"
                                val calendar = Calendar.getInstance(
                                    TimeZone.getDefault(),
                                    Locale.getDefault()
                                )
                                val currentLocalTime = calendar.time
                                val dateFormat: DateFormat =
                                    SimpleDateFormat("ZZZZZ", Locale.getDefault())

                                val timeZone: String = dateFormat.format(currentLocalTime)
                                calendar.set(Calendar.YEAR, year)
                                calendar.set(Calendar.MONTH, month)
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                calendar.set(Calendar.SECOND, 0)

                                val dateNow = Date()
                                if (calendar.time.time <= dateNow.time) {
                                    Toast.makeText(
                                        requireActivity(),
                                        "Cannot set past time. Please choose future time.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return
                                }

                                val dateFormat2 =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                                viewModel.deliveryAfterUi.value =
                                    dateFormat2.format(calendar.time) + " GMT$timeZone"
                                viewModel.deliveryAfterFinal.value =
                                    dateFormat2.format(calendar.time) + " GMT" + timeZone.replace(
                                        ":",
                                        ""
                                    )
                            }
                        })
                        timePicker.show(requireActivity().supportFragmentManager, null)
                    }
                })
                datePicker.show(requireActivity().supportFragmentManager, null)
            }

            binding.scheduleLayout.chooseTimeButton -> {
                val timePicker = TimePickerFragment()
                timePicker.setTimeSetListener(object :
                    TimePickerFragment.TimeSetListener {
                    override fun onTimeSet(hourOfDay: Int, minute: Int) {
                        val format = SimpleDateFormat("h:mmaa", Locale.getDefault())
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        viewModel.optimizedTimeZoneTime.value = format.format(calendar.time)
                    }
                })
                timePicker.show(requireActivity().supportFragmentManager, null)
            }

            binding.messageLayout.platformSettingsLayout.platformSettingsIos.addIosMediaButton -> {
                val iosMediaDialogLayoutBinding =
                    IosMediaDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                val mediaNumberTextView: TextView = iosMediaDialogLayoutBinding.mediaNumberTextView
                val doneButton: Button = iosMediaDialogLayoutBinding.doneButton
                val cancelButton: Button = iosMediaDialogLayoutBinding.cancelButton
                val valueEditText: EditText = iosMediaDialogLayoutBinding.valueEditText
                val keyEditText: EditText = iosMediaDialogLayoutBinding.keyEditText

                mediaNumberTextView.text = "Media ${(viewModel.medias.size + 1)}"
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(iosMediaDialogLayoutBinding.root)
                    .create()
                dialog.show()

                doneButton.setOnClickListener {
                    if (valueEditText.text.trim().isEmpty() || keyEditText.text.trim().isEmpty()) {
                        Toast.makeText(
                            requireActivity(),
                            "Please fill both key and value",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.addIosMedia(
                            keyEditText.text.trim().toString(),
                            valueEditText.text.trim().toString()
                        )
                        dialog.dismiss()
                    }
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }

            binding.messageLayout.advancedSettingsLayout.addAnotherFieldButton -> {
                val additionalDataDialogLayoutBinding =
                    AdditionalDataDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                val fieldNumberTextView: TextView =
                    additionalDataDialogLayoutBinding.fieldNumberTextView
                val doneButton: Button = additionalDataDialogLayoutBinding.doneButton
                val cancelButton: Button = additionalDataDialogLayoutBinding.cancelButton
                val valueEditText: EditText = additionalDataDialogLayoutBinding.valueEditText
                val keyEditText: EditText = additionalDataDialogLayoutBinding.keyEditText
                fieldNumberTextView.text = "Field ${(viewModel.fields.size + 1)}"
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(additionalDataDialogLayoutBinding.root)
                    .create()
                dialog.show()

                doneButton.setOnClickListener {
                    if (valueEditText.text.trim().isEmpty() || keyEditText.text.trim().isEmpty()) {
                        Toast.makeText(
                            requireActivity(),
                            "Please fill both key and value",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.addField(
                            (viewModel.fields.size + 1),
                            keyEditText.text.trim().toString(),
                            valueEditText.text.trim().toString()
                        )
                        dialog.dismiss()
                    }
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }

            binding.messageLayout.advancedSettingsLayout.addAnotherActionButton -> {
                if ((viewModel.actionButtons.size + 1) == 5) {
                    Toast.makeText(
                        requireActivity(),
                        "Maximum 4 action buttons are allowed",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val actionButtonDialogLayoutBinding =
                    ActionButtonDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                val buttonNumberTextView: TextView =
                    actionButtonDialogLayoutBinding.buttonNumberTextView
                val doneButton: Button = actionButtonDialogLayoutBinding.doneButton
                val cancelButton: Button = actionButtonDialogLayoutBinding.cancelButtonAction
                val actionIdEditText: EditText =
                    actionButtonDialogLayoutBinding.buttonActionIdEditText
                val textEditText: EditText = actionButtonDialogLayoutBinding.buttonTextEditText
                val iconEditText: EditText = actionButtonDialogLayoutBinding.buttonIconEditText

                buttonNumberTextView.text = "Button ${viewModel.actionButtons.size + 1}"
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(actionButtonDialogLayoutBinding.root)
                    .create()
                dialog.show()

                doneButton.setOnClickListener {
                    if (actionIdEditText.text.trim().isEmpty() || textEditText.text.trim()
                            .isEmpty()
                    ) {
                        Toast.makeText(
                            requireActivity(),
                            "Action ID and Text are mandatory",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        viewModel.addActionButton(
                            actionIdEditText.text.trim().toString(),
                            textEditText.text.trim().toString(),
                            iconEditText.text.trim().toString()
                        )
                        dialog.dismiss()
                    }
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

            }

            binding.audienceLayout.segmentsLayout.addAnotherSegmentButton -> {
                val segmentDialogLayoutBinding =
                    SegmentDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                val segmentNumberTextView: TextView =
                    segmentDialogLayoutBinding.segmentNumberTextView
                val doneButton: Button = segmentDialogLayoutBinding.doneButton
                val cancelButton: Button = segmentDialogLayoutBinding.cancelButton
                val segmentNameEditText: EditText = segmentDialogLayoutBinding.segmentNameEditText

                segmentNumberTextView.text = "Segment ${viewModel.includedSegments.size + 1}"
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(segmentDialogLayoutBinding.root)
                    .create()
                dialog.show()

                doneButton.setOnClickListener {
                    if (segmentNameEditText.text.trim().isEmpty()) {
                        Toast.makeText(
                            requireActivity(),
                            "Segment name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.includeSegment(segmentNameEditText.text.trim().toString())
                        dialog.dismiss()
                    }
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }

            binding.audienceLayout.segmentsLayout.addAnotherExcludeSegmentButton -> {
                val segmentDialogLayoutBinding =
                    SegmentDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                val segmentNumberTextView: TextView =
                    segmentDialogLayoutBinding.segmentNumberTextView
                val doneButton: Button = segmentDialogLayoutBinding.doneButton
                val cancelButton: Button = segmentDialogLayoutBinding.cancelButton
                val segmentNameEditText: EditText = segmentDialogLayoutBinding.segmentNameEditText

                segmentNumberTextView.text = "Segment ${viewModel.excludedSegments.size + 1}"
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(segmentDialogLayoutBinding.root)
                    .create()
                dialog.show()

                doneButton.setOnClickListener {
                    if (segmentNameEditText.text.trim().isEmpty()) {
                        Toast.makeText(
                            requireActivity(),
                            "Segment name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.excludeSegment(segmentNameEditText.text.trim().toString())
                        dialog.dismiss()
                    }
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }

            binding.audienceLayout.filtersLayout.addFilterButton -> {
                val filterDialogLayoutBinding =
                    FilterDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                val filterNumberTextView: TextView = filterDialogLayoutBinding.filterNumberTextView
                val doneButton: Button = filterDialogLayoutBinding.doneButton
                val cancelButton: Button = filterDialogLayoutBinding.cancelButton
                val operatorRelativeLayout: RelativeLayout =
                    filterDialogLayoutBinding.operatorRelativeLayout
                val operatorSpinner: Spinner = filterDialogLayoutBinding.operatorSpinner
                val fieldSpinner: Spinner = filterDialogLayoutBinding.fieldSpinner
                val keyLinearLayout: LinearLayout = filterDialogLayoutBinding.keyLinearLayout
                val relationRelativeLayout: RelativeLayout =
                    filterDialogLayoutBinding.relationRelativeLayout
                val hoursAgoLinearLayout: LinearLayout =
                    filterDialogLayoutBinding.hoursAgoLinearLayout
                val valueLinearLayout: LinearLayout = filterDialogLayoutBinding.valueLinearLayout
                val radiusLinearLayout: LinearLayout = filterDialogLayoutBinding.radiusLinearLayout
                val latitudeLinearLayout: LinearLayout =
                    filterDialogLayoutBinding.latitudeLinearLayout
                val longitudeLinearLayout: LinearLayout =
                    filterDialogLayoutBinding.longitudeLinearLayout
                val relationSpinner: Spinner = filterDialogLayoutBinding.relationSpinner

                val filterKeyEditText: EditText = filterDialogLayoutBinding.filterKeyEditText
                val filterValueEditText: EditText = filterDialogLayoutBinding.filterValueEditText
                val filterHoursAgoEditText: EditText =
                    filterDialogLayoutBinding.filterHoursAgoEditText
                val filterRadiusEditText: EditText = filterDialogLayoutBinding.filterRadiusEditText
                val filterLatitudeEditText: EditText =
                    filterDialogLayoutBinding.filterLatitudeEditText
                val filterLongitudeEditText: EditText =
                    filterDialogLayoutBinding.filterLongitudeEditText

                if (viewModel.filters.size == 0) {
                    operatorRelativeLayout.visibility = GONE
                } else {
                    operatorRelativeLayout.visibility = VISIBLE
                }

                val operatorAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.filter_operators, android.R.layout.simple_spinner_item
                )
                operatorAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
                operatorSpinner.adapter = operatorAdapter

                val fieldAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.filter_fields_list, android.R.layout.simple_spinner_item
                )
                fieldAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
                fieldSpinner.adapter = fieldAdapter

                fieldSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        manage(
                            position, requireContext(),
                            keyLinearLayout, relationRelativeLayout, hoursAgoLinearLayout,
                            valueLinearLayout, radiusLinearLayout, latitudeLinearLayout,
                            longitudeLinearLayout, relationSpinner
                        )
                    }
                }

                filterNumberTextView.text = "Filter ${viewModel.filters.size + 1}"
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(filterDialogLayoutBinding.root)
                    .create()
                dialog.show()

                doneButton.setOnClickListener {
                    var filter: Filter? = Filter()
                    val filterFieldsListForApi =
                        resources.getStringArray(R.array.filter_fields_list_for_api)
                    filter!!.field = filterFieldsListForApi[fieldSpinner.selectedItemPosition]

                    filter = verify(
                        filter, requireActivity(), fieldSpinner, filterValueEditText,
                        filterKeyEditText, relationSpinner, filterHoursAgoEditText,
                        filterRadiusEditText, filterLatitudeEditText, filterLongitudeEditText
                    )

                    filter?.let {
                        if (viewModel.filters.size != 0) {
                            it.operator = operatorSpinner.selectedItem as String
                        }

                        viewModel.addFilter(it)
                        dialog.dismiss()
                    }
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }

            binding.messageLayout.advancedSettingsExtender -> {
                viewModel.isAdvancedSettingsExpanded = if (viewModel.isAdvancedSettingsExpanded) {
                    binding.messageLayout.arrow2.animate().rotation(0f)
                    binding.messageLayout.advancedSettingsLayout.root.visibility =
                        GONE
                    false
                } else {
                    binding.messageLayout.arrow2.animate().rotation(90f)
                    binding.messageLayout.advancedSettingsLayout.root.visibility =
                        VISIBLE
                    true
                }
            }

            binding.messageLayout.platformSettingsExtender -> {
                viewModel.isPlatformSettingsExpanded = if (viewModel.isPlatformSettingsExpanded) {
                    binding.messageLayout.arrow1.animate().rotation(0f)
                    binding.messageLayout.platformSettingsLayout.root.visibility =
                        GONE
                    false
                } else {
                    binding.messageLayout.arrow1.animate().rotation(90f)
                    binding.messageLayout.platformSettingsLayout.root.visibility =
                        VISIBLE
                    true
                }
            }
        }
    }

    private fun processNotification() {
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (notificationManager.areNotificationsEnabled().not()) {
                Toast.makeText(
                    requireContext(),
                    "Notifications are disabled. Please enable them in settings",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (notificationManager.areNotificationsPaused()) {
                Toast.makeText(
                    requireContext(),
                    "Notifications are paused. Please enable them in settings",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val notification = buildNotification(
            binding,
            requireActivity(),
            viewModel,
            deliverAfterFinal,
            optimizedTimeZoneTime
        ) ?: return
        notification.appId = Application.appId
        notification.includedSegments = null
        notification.excludedSegments = null
        notification.includePlayerIds = null
        notification.filters = null

        previewNotification(notification)
    }

    private val channelId = "preview_notifications"
    private val channelName = "Preview Notifications"

    @Suppress("DEPRECATION")
    private fun previewNotification(payload: CreateNotification) {

        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            var channel = notificationManager.getNotificationChannel(channelId)
            if (channel == null) {
                channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = "In this channel you will receive previews of notification."
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
            builder = NotificationCompat.Builder(requireContext(), channelId).apply {
                setContentTitle(payload.heading?.en)
                setContentText(payload.content?.en)
                setSmallIcon(com.onesignal.R.drawable.ic_os_notification_fallback_white_24dp)
                setDefaults(Notification.DEFAULT_ALL)
                setAutoCancel(true)
                setTicker(payload.heading?.en)
                setStyle(NotificationCompat.BigTextStyle())
                payload.androidAccentColor?.let {
                    color = BigInteger(it, 16).toInt()
                }
                payload.androidLockScreenVisibility?.let {
                    setVisibility(it)
                }
                payload.androidLedColor?.let {
                    val ledColor = BigInteger(it, 16)
                    setLights(ledColor.toInt(), 2000, 5000)
                }
                payload.buttons?.let {
                    it.forEach { button ->
                        addAction(button.icon?.toIntOrNull() ?: 0, button.text!!, null)
                    }
                }
            }
            payload.apply {
                CoroutineScope(Dispatchers.IO).launch {
                    var largeIconBitmap: Bitmap? = null
                    var bigPictureBitmap: Bitmap? = null

                    if (!largeIcon.isNullOrBlank() || !bigPicture.isNullOrBlank()) {
                        viewModel.result.postValue(Event(Resource.loading(null, true)))
                    }
                    largeIcon?.let {
                        try {
                            val bitmap = BitmapFactory.decodeStream(
                                URL(it.trim()).openConnection().getInputStream()
                            )
                            largeIconBitmap = resizeBitmapForLargeIconArea(requireContext(), bitmap)
                        } catch (_: Exception) {
                        }
                    }

                    bigPicture?.let {
                        try {
                            bigPictureBitmap = BitmapFactory.decodeStream(
                                URL(it.trim()).openConnection().getInputStream()
                            )
                        } catch (_: Exception) {
                        }
                    }

                    withContext(Dispatchers.Main) {
                        largeIconBitmap?.let {
                            builder.setLargeIcon(it)
                        }
                        bigPictureBitmap?.let {
                            builder.setStyle(
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(it).setSummaryText(payload.content?.en)
                            )
                        }
                        notificationManager.notify(Random.nextInt(), builder.build())
                        viewModel.result.value = Event(Resource.success(null, true))
                    }
                }
            }

        } else {
            builder = NotificationCompat.Builder(requireContext()).apply {
                setContentTitle(payload.heading?.en)
                setContentText(payload.content?.en)
                setSmallIcon(com.onesignal.R.drawable.ic_os_notification_fallback_white_24dp)
                setDefaults(Notification.DEFAULT_ALL)
                setAutoCancel(true)
                setTicker(payload.heading?.en)
                priority = Notification.PRIORITY_DEFAULT
                setStyle(NotificationCompat.BigTextStyle())
                payload.androidAccentColor?.let {
                    color = BigInteger(it, 16).toInt()
                }
                payload.androidLockScreenVisibility?.let {
                    setVisibility(it)
                }
                payload.androidLedColor?.let {
                    val ledColor = BigInteger(it, 16)
                    setLights(ledColor.toInt(), 2000, 5000)
                }
                payload.buttons?.let {
                    it.forEach { button ->
                        addAction(button.icon?.toIntOrNull() ?: 0, button.text!!, null)
                    }
                }
            }

            payload.apply {
                CoroutineScope(Dispatchers.IO).launch {
                    var largeIconBitmap: Bitmap? = null
                    var bigPictureBitmap: Bitmap? = null

                    if (!largeIcon.isNullOrBlank() || !bigPicture.isNullOrBlank()) {
                        viewModel.result.postValue(Event(Resource.loading(null, true)))
                    }

                    largeIcon?.let {
                        try {
                            val bitmap = BitmapFactory.decodeStream(
                                URL(it.trim()).openConnection().getInputStream()
                            )
                            largeIconBitmap = resizeBitmapForLargeIconArea(requireContext(), bitmap)
                        } catch (_: Exception) {
                        }
                    }

                    bigPicture?.let {
                        try {
                            bigPictureBitmap = BitmapFactory.decodeStream(
                                URL(it.trim()).openConnection().getInputStream()
                            )
                        } catch (_: Exception) {
                        }
                    }

                    withContext(Dispatchers.Main) {
                        largeIconBitmap?.let {
                            builder.setLargeIcon(it)
                        }
                        bigPictureBitmap?.let {
                            builder.setStyle(
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(it).setSummaryText(payload.content?.en)
                            )
                        }
                        notificationManager.notify(Random.nextInt(), builder.build())
                        viewModel.result.value = Event(Resource.success(null, true))
                    }
                }
            }
        }
    }
}