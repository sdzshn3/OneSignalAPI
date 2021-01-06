package com.sdzshn3.onesignalapi.ui.accountSetup

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sdzshn3.onesignalapi.EncryptedPrefManager
import com.sdzshn3.onesignalapi.PrefManager
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_account_setup.*
import java.util.*

@Suppress("NAME_SHADOWING")
class AccountSetupFragment : Fragment(R.layout.fragment_account_setup) {

    private lateinit var prefManager: PrefManager
    private lateinit var encryptedPrefManager: EncryptedPrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefManager = PrefManager(requireContext())
        encryptedPrefManager = EncryptedPrefManager(requireContext())

        val restApiKey = prefManager.restApiKey
        val appId = prefManager.oneSignalAppId

        val encryptedRestApiKey = encryptedPrefManager.restApiKey
        val encryptedAppId = encryptedPrefManager.oneSignalAppId

        if (encryptedRestApiKey.isNullOrBlank() || encryptedAppId.isNullOrBlank()) {
            if (restApiKey != null  && restApiKey.isNotBlank() && appId != null && appId.isNotBlank()) {
                encryptedPrefManager.oneSignalAppId = appId
                encryptedPrefManager.restApiKey = restApiKey
                prefManager.removeAllPrefs()
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        } else {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        sourceCodeButton.setOnClickListener {
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

        submitButton.setOnClickListener {
            val restApiKey = restApiKeyEditText.text.toString().trim()
            val appId = appIdEditText.text.toString().trim()

            if (restApiKey.isBlank() && appId.isBlank()) {
                Toast.makeText(
                    requireActivity(),
                    "Please fill both App ID and REST API Key",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
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

            encryptedPrefManager.oneSignalAppId = appId
            encryptedPrefManager.restApiKey = restApiKey

            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

}