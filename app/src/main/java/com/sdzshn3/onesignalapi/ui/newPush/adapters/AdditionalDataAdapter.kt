package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.databinding.AdditionalDataItemLayoutBinding
import com.sdzshn3.onesignalapi.model.Field
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener

@SuppressLint("SetTextI18n")
class AdditionalDataAdapter(
    private val context: Context,
    private var fields: List<Field>
) : RecyclerView.Adapter<AdditionalDataAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(val binding: AdditionalDataItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdditionalDataItemLayoutBinding.inflate(
                LayoutInflater.from(context),
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
        holder.binding.apply {
            fieldNumberTextView.text = "FIELD " + fields[position].number.toString()
            keyTextView.text = fields[position].key
            valueTextView.text = fields[position].value

            deleteFieldButton.setOnClickListener {
                deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
            }
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}