package com.sdzshn3.onesignalapi.ui.notificationDetail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.sdzshn3.onesignalapi.EncryptedPrefManager
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.databinding.FragmentNotificationDetailBinding
import com.sdzshn3.onesignalapi.model.Notification
import com.sdzshn3.onesignalapi.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class NotificationDetailFragment : Fragment(R.layout.fragment_notification_detail) {

    private val args: NotificationDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentNotificationDetailBinding
    private lateinit var notification: Notification
    private val viewModel: NotificationDetailViewModel by viewModels()
    private lateinit var prefManager: EncryptedPrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNotificationDetailBinding.bind(view)

        prefManager = EncryptedPrefManager(requireContext())
        if (args.notification != null) {
            notification = args.notification!!
            populateData()
        } else {
            if (viewModel.notification.value == null) {
                val notificationId = args.id!!
                viewModel.load(
                    "notifications/$notificationId",
                    prefManager.oneSignalAppId!!,
                    "Basic ${prefManager.restApiKey!!}"
                )
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            val notificationId = if (args.notification != null) {
                args.notification!!.id
            } else {
                args.id!!
            }
            viewModel.load(
                "notifications/$notificationId",
                prefManager.oneSignalAppId!!,
                "Basic ${prefManager.restApiKey!!}"
            )
        }
        viewModel.notification.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    it.message?.let { it1 ->
                        Toast.makeText(requireActivity(), it1, Toast.LENGTH_SHORT).show()
                    }
                    findNavController().navigateUp()
                }
                Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    notification = it.data!!
                    populateData()
                }
            }

        }

    }

    private fun populateData() {
        binding.deliveredCountTV.text = notification.successful.toString()
        binding.messageTV.text = notification.contents.en
        binding.titleTV.text = notification.headings.en ?: "<No Title>"
        setupDeliveryPieChart()
        setupPlatformPieChart()
    }

    private fun setupPlatformPieChart() {
        binding.platformPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.platformPieChart.dragDecelerationFrictionCoef = 0.95f
        binding.platformPieChart.centerText = "Platform Statistics"
        binding.platformPieChart.isDrawHoleEnabled = true
        binding.platformPieChart.description.isEnabled = false
        binding.platformPieChart.setHoleColor(Color.WHITE)

        binding.platformPieChart.setTransparentCircleColor(Color.WHITE)
        binding.platformPieChart.setTransparentCircleAlpha(110)

        binding.platformPieChart.holeRadius = 45f
        binding.platformPieChart.transparentCircleRadius = 50f

        binding.platformPieChart.setDrawCenterText(false)
        binding.platformPieChart.setCenterTextSize(20f)
        binding.platformPieChart.rotationAngle = 0f

        binding.platformPieChart.isRotationEnabled = true
        binding.platformPieChart.isHighlightPerTapEnabled = true
        binding.platformPieChart.animateY(1400, Easing.EaseInOutQuad)

        val entries: ArrayList<PieEntry> = ArrayList()

        var android = 0
        notification.platformDeliveryStats.android?.let {
            android = it.successful + it.errored + it.failed
        }
        var ios = 0
        notification.platformDeliveryStats.ios?.let {
            ios = it.successful + it.errored + it.failed
        }
        var webPush = 0
        notification.platformDeliveryStats.chromeWebPush?.let {
            webPush += it.successful + it.errored + it.failed
        }
        notification.platformDeliveryStats.edgeWebPush?.let {
            webPush += it.successful + it.errored + it.failed
        }
        notification.platformDeliveryStats.firefoxWebPush?.let {
            webPush += it.successful + it.errored + it.failed
        }
        notification.platformDeliveryStats.safariWebPush?.let {
            webPush += it.successful + it.errored + it.failed
        }
        val totalCompleted = notification.successful +
                notification.errored +
                notification.failed
        val others = (totalCompleted) - (android + ios + webPush)

        //android = 20
        //ios = 8
        //webPush = 9
        //others = 14
        if (android != 0) {
            entries.add(PieEntry(android.toFloat(), "Android"))
        }
        if (ios != 0) {
            entries.add(PieEntry(ios.toFloat(), "iOS"))
        }
        if (webPush != 0) {
            entries.add(PieEntry(webPush.toFloat(), "Web Push"))
        }
        if (others != 0) {
            entries.add(PieEntry(others.toFloat(), "Others"))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.valueLinePart1OffsetPercentage = 80f
        val colors: ArrayList<Int> = ArrayList()
        ColorTemplate.COLORFUL_COLORS.forEach {
            colors.add(it)
        }
        dataSet.colors = colors
        val data = PieData(dataSet)
        val formatter = PercentFormatter()
        formatter.mFormat = DecimalFormat("###,###,##0")
        data.setValueFormatter(formatter)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)
        binding.platformPieChart.data = data
        binding.platformPieChart.highlightValues(null)
        binding.platformPieChart.invalidate()
    }

    private fun setupDeliveryPieChart() {
        binding.pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.pieChart.dragDecelerationFrictionCoef = 0.95f
        binding.pieChart.centerText = "Delivery Statistics"
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setHoleColor(Color.WHITE)

        binding.pieChart.setTransparentCircleColor(Color.WHITE)
        binding.pieChart.setTransparentCircleAlpha(110)

        binding.pieChart.holeRadius = 45f
        binding.pieChart.transparentCircleRadius = 50f

        binding.pieChart.setDrawCenterText(false)
        binding.pieChart.setCenterTextSize(20f)
        binding.pieChart.rotationAngle = 0f

        binding.pieChart.isRotationEnabled = true
        binding.pieChart.isHighlightPerTapEnabled = true
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)

        val entries: ArrayList<PieEntry> = ArrayList()

        entries.add(PieEntry(notification.successful.toFloat(), "Delivered"))
        if (notification.errored != 0) {
            entries.add(PieEntry(notification.errored.toFloat(), "Errored"))
        }
        if (notification.failed != 0) {
            entries.add(PieEntry(notification.failed.toFloat(), "Unsubscribed"))
        }
        if (notification.remaining != 0) {
            entries.add(PieEntry(notification.remaining.toFloat(), "Remaining"))
        }
        //entries.add(PieEntry(2f, "Errored"))
        //entries.add(PieEntry(1f, "Unsubscribed"))
        //entries.add(PieEntry(3f, "Remaining"))

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.valueLinePart1OffsetPercentage = 80f
        val colors: ArrayList<Int> = ArrayList()
        colors.add(ColorTemplate.rgb("#2ecc71"))
        colors.add(ColorTemplate.rgb("#e74c3c"))
        colors.add(ColorTemplate.rgb("#f1c40f"))
        colors.add(ColorTemplate.rgb("#3498db"))

        ColorTemplate.MATERIAL_COLORS.forEach {
            colors.add(it)
        }
        dataSet.colors = colors
        val data = PieData(dataSet)
        val formatter = PercentFormatter()
        formatter.mFormat = DecimalFormat("###,###,##0")
        data.setValueFormatter(formatter)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)
        binding.pieChart.data = data
        binding.pieChart.highlightValues(null)
        binding.pieChart.invalidate()
    }
}