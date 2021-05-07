package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.databinding.FilterItemLayoutBinding
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import com.sdzshn3.onesignalapi.oneSignalPOJO.Filter

class FiltersAdapter(
    private val context: Context
) : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null
    private var filters: List<Filter> = ArrayList()

    class ViewHolder(val binding: FilterItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FilterItemLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    fun submitList(_filters: List<Filter>) {
        filters = _filters
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val currentFilter = filters[position]

            if (currentFilter.operator.isNullOrBlank()) {
                operatorLayout.visibility = GONE
            } else {
                operatorLayout.visibility = VISIBLE
                filterOperatorTextView.text = currentFilter.operator
            }

            if (currentFilter.field.isNullOrBlank()) {
                fieldLayout.visibility = GONE
            } else {
                fieldLayout.visibility = VISIBLE
                filterFieldTextView.text = currentFilter.field
            }

            if (currentFilter.hoursAgo.isNullOrBlank()) {
                hoursAgoLayout.visibility = GONE
            } else {
                hoursAgoLayout.visibility = VISIBLE
                filterHoursAgoTextView.text = currentFilter.hoursAgo
            }

            if (currentFilter.key.isNullOrBlank()) {
                keyLayout.visibility = GONE
            } else {
                keyLayout.visibility = VISIBLE
                filterKeyTextView.text = currentFilter.key
            }

            if (currentFilter.latitude.isNullOrBlank()) {
                latitudeLayout.visibility = GONE
            } else {
                latitudeLayout.visibility = VISIBLE
                filterLatitudeTextView.text = currentFilter.latitude
            }

            if (currentFilter.longitude.isNullOrBlank()) {
                longitudeLayout.visibility = GONE
            } else {
                longitudeLayout.visibility = VISIBLE
                filterLongitudeTextView.text = currentFilter.longitude
            }

            if (currentFilter.radius.isNullOrBlank()) {
                radiusLayout.visibility = GONE
            } else {
                radiusLayout.visibility = VISIBLE
                filterRadiusTextView.text = currentFilter.radius
            }

            if (currentFilter.relation.isNullOrBlank()) {
                relationLayout.visibility = GONE
            } else {
                relationLayout.visibility = VISIBLE
                filterRelationTextView.text = currentFilter.relation
            }

            if (currentFilter.value.isNullOrBlank()) {
                valueLayout.visibility = GONE
            } else {
                valueLayout.visibility = VISIBLE
                filterValueTextView.text = currentFilter.value
            }

            filterNumberTextView.text = "Filter ${position + 1}"

            if ((filters.size - 1) == position) {
                deleteFilterButton.visibility = VISIBLE
            } else {
                deleteFilterButton.visibility = GONE
            }

            deleteFilterButton.setOnClickListener {
                deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
            }
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }

}