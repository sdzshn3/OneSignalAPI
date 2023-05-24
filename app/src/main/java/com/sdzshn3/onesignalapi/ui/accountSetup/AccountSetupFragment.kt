package com.sdzshn3.onesignalapi.ui.accountSetup

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sdzshn3.onesignalapi.EncryptedPrefManager
import com.sdzshn3.onesignalapi.PrefManager
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.databinding.FragmentAccountSetupBinding
import com.sdzshn3.onesignalapi.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@Suppress("NAME_SHADOWING")
@AndroidEntryPoint
class AccountSetupFragment : Fragment(R.layout.fragment_account_setup) {

    private lateinit var prefManager: PrefManager
    private lateinit var encryptedPrefManager: EncryptedPrefManager
    private val viewModel: AccountSetupViewModel by viewModels()
    private lateinit var binding: FragmentAccountSetupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAccountSetupBinding.bind(view)
        prefManager = PrefManager(requireContext())
        encryptedPrefManager = EncryptedPrefManager(requireContext())

        val restApiKey = prefManager.restApiKey
        val appId = prefManager.oneSignalAppId

        val encryptedRestApiKey = encryptedPrefManager.restApiKey
        val encryptedAppId = encryptedPrefManager.oneSignalAppId

        if (restApiKey != null && restApiKey.isNotBlank() &&
            appId != null && appId.isNotBlank()
        ) {
            viewModel.addApp("App1", appId, restApiKey)
            prefManager.removeAllPrefs()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        } else if (encryptedRestApiKey != null && encryptedRestApiKey.isNotBlank() &&
            encryptedAppId != null && encryptedAppId.isNotBlank()
        ) {
            viewModel.addApp("App1", encryptedAppId, encryptedRestApiKey)
            encryptedPrefManager.deleteAll()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        } else {
            observeViewModel()
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.sourceCodeButton.setOnClickListener {
            val sourceCodeUrl = "https://github.com/sdzshn3/OneSignalAPI"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(sourceCodeUrl)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Browsers not found to open link", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.submitButton.setOnClickListener {
            val restApiKey = binding.restApiKeyEditText.text.toString().trim()
            val appId = binding.appIdEditText.text.toString().trim()
            val appName = binding.appNameEditText.text.toString().trim()

            if (restApiKey.isBlank() && appId.isBlank() && appName.isBlank()) {
                Toast.makeText(
                    requireActivity(),
                    "Please fill all fields",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else if (appName.isBlank()) {
                Toast.makeText(
                    requireActivity(),
                    "Please fill App Name",
                    Toast.LENGTH_LONG
                ).show()
            } else if (appId.isBlank()) {
                Toast.makeText(
                    requireActivity(),
                    "Please fill App ID",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else if (restApiKey.isBlank()) {
                Toast.makeText(
                    requireActivity(),
                    "Please fill REST API Key",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else if (appId.length < 36) {
                Toast.makeText(
                    requireActivity(),
                    "App ID length should be 36",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else if (restApiKey.length < 48) {
                Toast.makeText(
                    requireActivity(),
                    "API KEY length should be 48",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            viewModel.addApp(appName, appId, restApiKey)
        }
    }

    private fun observeViewModel() = viewModel.apply {
        appsList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                binding.splash.isVisible = false
            }
        }
    }

}