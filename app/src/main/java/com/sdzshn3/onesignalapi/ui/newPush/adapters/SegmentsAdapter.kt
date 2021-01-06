package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import kotlinx.android.synthetic.main.segment_item_layout.view.*

class SegmentsAdapter(
    private val context: Context,
    private var segments: List<String>
) : RecyclerView.Adapter<SegmentsAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val segmentNumberTextView: TextView = view.segmentNumberTextView
        val deleteSegmentButton: TextView = view.deleteSegmentButton
        val segmentTextView: TextView = view.segmentTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.segment_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return segments.size
    }

    fun submitList(_segments: List<String>) {
        segments = _segments
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.segmentNumberTextView.text = "SEGMENT " + (position + 1)
        holder.segmentTextView.text = segments[position]

        holder.deleteSegmentButton.setOnClickListener {
            deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}