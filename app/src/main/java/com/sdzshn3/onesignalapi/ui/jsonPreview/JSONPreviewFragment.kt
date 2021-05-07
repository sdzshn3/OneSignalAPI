package com.sdzshn3.onesignalapi.ui.jsonPreview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.databinding.FragmentJsonPreviewBinding

class JSONPreviewFragment : Fragment(R.layout.fragment_json_preview) {

    private lateinit var binding: FragmentJsonPreviewBinding
    private val args: JSONPreviewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentJsonPreviewBinding.bind(view)
        setHasOptionsMenu(true)

        val string = args.message
        binding.jsonTV.text = string
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.json_preview_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.copy -> {
                val clipBoard =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "OneSignal API Notification JSON",
                    arguments?.getString("message")
                )
                clipBoard.setPrimaryClip(clipData)
                Toast.makeText(requireActivity(), "Copied to Clipboard", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
