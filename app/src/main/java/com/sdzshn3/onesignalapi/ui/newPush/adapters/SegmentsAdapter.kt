package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.databinding.SegmentItemLayoutBinding
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener

class SegmentsAdapter(
    private val context: Context,
    private var segments: List<String>
) : RecyclerView.Adapter<SegmentsAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(val binding: SegmentItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SegmentItemLayoutBinding.inflate(
                LayoutInflater.from(context),
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
        holder.binding.apply {
            segmentNumberTextView.text = "SEGMENT " + (position + 1)
            segmentTextView.text = segments[position]

            deleteSegmentButton.setOnClickListener {
                deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
            }
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}