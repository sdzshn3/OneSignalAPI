package com.sdzshn3.onesignalapi.ui.notificationHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.sdzshn3.onesignalapi.EncryptedPrefManager
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.databinding.FragmentNotificationHistoryBinding
import com.sdzshn3.onesignalapi.model.Notification
import com.sdzshn3.onesignalapi.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationHistoryFragment : Fragment() {

    private val viewModel: NotificationHistoryViewModel by viewModels()
    private lateinit var prefManager: EncryptedPrefManager
    private lateinit var adapter: NotificationHistoryAdapter
    private var _binding: FragmentNotificationHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var notification: Notification
    private var buttonClickCount = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (_binding == null) {
            _binding = FragmentNotificationHistoryBinding.inflate(inflater, container, false)
            _binding!!.root
        } else {
            _binding!!.root
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.notificationHistoryBanner.loadAd(AdRequest.Builder().build())

        interstitialAd = InterstitialAd(requireContext())
        interstitialAd.adUnitId = getString(R.string.notificationHistoryFragInterstitialAdUnitId)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                interstitialAd.loadAd(AdRequest.Builder().build())
                val directions =
                    NotificationHistoryFragmentDirections.actionNotificationHistoryFragmentToNotificationDetailFragment(
                        notification,
                        null
                    )
                findNavController().navigate(directions)
            }
        }
        interstitialAd.loadAd(AdRequest.Builder().build())

        prefManager = EncryptedPrefManager(requireContext())
        adapter = NotificationHistoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        observeViewModels()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadAllNotifications(
                prefManager.oneSignalAppId!!,
                "Basic ${prefManager.restApiKey}"
            )
        }
        adapter.setOnItemClickLister {
            if (interstitialAd.isLoaded && buttonClickCount > 1) {
                notification = it
                buttonClickCount = 0
                interstitialAd.show()
            } else {
                buttonClickCount++
                val directions =
                    NotificationHistoryFragmentDirections.actionNotificationHistoryFragmentToNotificationDetailFragment(
                        it,
                        null
                    )
                findNavController().navigate(directions)
            }
        }
    }

    private fun observeViewModels() {
        viewModel.allNotifications.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.LOADING -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    it.message?.let { it1 -> Toast.makeText(requireActivity(), it1, Toast.LENGTH_LONG).show() }
                    findNavController().navigateUp()
                }
                Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    adapter.submitList(it.data!!.notifications)
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        _binding?.notificationHistoryBanner?.pause()
    }

    override fun onResume() {
        super.onResume()
        _binding?.notificationHistoryBanner?.resume()
    }

}