package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.model.Field
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import kotlinx.android.synthetic.main.additional_data_item_layout.view.*

@SuppressLint("SetTextI18n")
class AdditionalDataAdapter(
    private val context: Context,
    private var fields: List<Field>
) : RecyclerView.Adapter<AdditionalDataAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fieldNumberTextView: TextView = view.fieldNumberTextView
        val deleteFieldButton: TextView = view.deleteFieldButton
        val keyEditText: TextView = view.keyTextView
        val valueEditText: TextView = view.valueTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.additional_data_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return fields.size
    }

    fun submitList(fieldsList: List<Field>) {
        fields = fieldsList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fieldNumberTextView.text = "FIELD " + fields[position].number.toString()
        holder.keyEditText.text = fields[position].key
        holder.valueEditText.text = fields[position].value

        holder.deleteFieldButton.setOnClickListener {
            deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}