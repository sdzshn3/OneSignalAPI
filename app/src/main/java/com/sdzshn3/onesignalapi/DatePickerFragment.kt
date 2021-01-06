package com.sdzshn3.onesignalapi

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var dateSetListener: DateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val picker = DatePickerDialog(activity as Context, this, year, month, day)
        picker.datePicker.minDate = calendar.timeInMillis
        picker.datePicker.tag = arguments?.getString("tag")
        return picker
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateSetListener?.onDateSet(year, month, dayOfMonth)
    }

    fun setDateSetListener(dateSetListener: DateSetListener) {
        this.dateSetListener = dateSetListener
    }

    interface DateSetListener {
        fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
    }
}