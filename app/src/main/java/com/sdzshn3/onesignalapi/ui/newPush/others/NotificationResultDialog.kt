package com.sdzshn3.onesignalapi.ui.newPush.others

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdzshn3.onesignalapi.nlog

class NotificationResultDialog : DialogFragment() {

    companion object {
        fun newInstance(
            message: String,
            title: String,
            success: Boolean
        ): NotificationResultDialog {
            val args = Bundle()
            args.putString("message", message)
            args.putString("title", title)
            args.putBoolean("success", success)
            val dialog = NotificationResultDialog()
            dialog.arguments = args
            return dialog
        }
    }

    private var moreDetailsListener: (() -> Unit)? = null
    private var dialogClosedListener: (() -> Unit)? = null

    fun setMoreDetailsListener(listener: () -> Unit) {
        moreDetailsListener = listener
    }

    fun setOnDialogClosedListener(listener: () -> Unit) {
        dialogClosedListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = requireArguments().getString("message")
        val title = requireArguments().getString("title")
        val success = requireArguments().getBoolean("success")

        return MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage(message)
            setTitle(title)
            setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
            }
            if (success) {
                setNeutralButton("More Details") { _, _ ->
                    moreDetailsListener?.let {
                        it()
                    }
                }
            }

        }.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        nlog("onCancel")
        dialogClosedListener?.let {
            it()
        }
    }
}