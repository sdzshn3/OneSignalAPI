package com.sdzshn3.onesignalapi.ui.jsonPreview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.databinding.FragmentJsonPreviewBinding

class JSONPreviewFragment : Fragment() {

    private var _binding: FragmentJsonPreviewBinding? = null
    private val binding get() = _binding!!
    private val args: JSONPreviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJsonPreviewBinding.inflate(inflater)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.jsonPreviewBanner.loadAd(AdRequest.Builder().build())

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

    override fun onPause() {
        super.onPause()
        _binding?.jsonPreviewBanner?.pause()
    }

    override fun onResume() {
        super.onResume()
        _binding?.jsonPreviewBanner?.resume()
    }
}
