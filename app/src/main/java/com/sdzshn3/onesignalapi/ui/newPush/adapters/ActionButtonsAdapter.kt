package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.model.ActionButton
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import kotlinx.android.synthetic.main.action_button_item_layout.view.*

@SuppressLint("SetTextI18n")
class ActionButtonsAdapter(
    private val context: Context,
    private var actionButtons: List<ActionButton>
) : RecyclerView.Adapter<ActionButtonsAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonNumberTextView: TextView = view.buttonNumberTextView
        val deleteButtonButton: TextView = view.deleteButtonButton
        val buttonActionIdTextView: TextView = view.buttonActionIdTextView
        val buttonTextTextView: TextView = view.buttonTextTextView
        val buttonIconTextView: TextView = view.buttonIconTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.action_button_item_layout,
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
        holder.buttonNumberTextView.text = "BUTTON ${position+1}"
        holder.buttonActionIdTextView.text = actionButtons[position].id
        holder.buttonTextTextView.text = actionButtons[position].text
        holder.buttonIconTextView.text = actionButtons[position].icon

        holder.deleteButtonButton.setOnClickListener {
            deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}