package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.databinding.ActionButtonItemLayoutBinding
import com.sdzshn3.onesignalapi.model.ActionButton
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener

@SuppressLint("SetTextI18n")
class ActionButtonsAdapter(
    private val context: Context,
    private var actionButtons: List<ActionButton>
) : RecyclerView.Adapter<ActionButtonsAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(val binding: ActionButtonItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ActionButtonItemLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return actionButtons.size
    }

    fun submitList(actionButtonList: List<ActionButton>) {
        actionButtons = actionButtonList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            buttonNumberTextView.text = "BUTTON ${position+1}"
            buttonActionIdTextView.text = actionButtons[position].id
            buttonTextTextView.text = actionButtons[position].text
            buttonIconTextView.text = actionButtons[position].icon

            deleteButtonButton.setOnClickListener {
                deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
            }
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}