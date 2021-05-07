package com.sdzshn3.onesignalapi.ui.notificationHistory

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdzshn3.onesignalapi.EncryptedPrefManager
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.databinding.FragmentNotificationHistoryBinding
import com.sdzshn3.onesignalapi.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationHistoryFragment : Fragment(R.layout.fragment_notification_history) {

    private val viewModel: NotificationHistoryViewModel by viewModels()
    private lateinit var prefManager: EncryptedPrefManager
    private lateinit var adapter: NotificationHistoryAdapter
    private lateinit var binding: FragmentNotificationHistoryBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationHistoryBinding.bind(view)

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
            val directions =
                NotificationHistoryFragmentDirections.actionNotificationHistoryFragmentToNotificationDetailFragment(
                    it,
                    null
                )
            findNavController().navigate(directions)
        }
    }

    private fun observeViewModels() {
        viewModel.allNotifications.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    it.message?.let { it1 ->
                        Toast.makeText(
                            requireActivity(),
                            it1,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    findNavController().navigateUp()
                }
                Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    adapter.submitList(it.data!!.notifications)
                }
            }
        }

    }
}