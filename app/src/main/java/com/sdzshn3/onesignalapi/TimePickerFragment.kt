package com.sdzshn3.onesignalapi

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var timeSetListener: TimeSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timeSetListener?.onTimeSet(hourOfDay, minute)
    }

    fun setTimeSetListener(timeSetListener: TimeSetListener) {
        this.timeSetListener = timeSetListener
    }

    interface TimeSetListener {
        fun onTimeSet(hourOfDay: Int, minute: Int)
    }
}