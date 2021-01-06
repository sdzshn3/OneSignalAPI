package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import com.sdzshn3.onesignalapi.oneSignalPOJO.Filter
import kotlinx.android.synthetic.main.filter_item_layout.view.*

class FiltersAdapter(
    private val context: Context
) : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null
    private var filters: List<Filter> = ArrayList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deleteFilterButton: TextView = view.deleteFilterButton
        val filterNumberTextView: TextView = view.filterNumberTextView
        val operatorLayout: RelativeLayout = view.operatorLayout
        val filterOperatorTextView: TextView = view.filterOperatorTextView
        val fieldLayout: RelativeLayout = view.fieldLayout
        val filterFieldTextView: TextView = view.filterFieldTextView
        val keyLayout: RelativeLayout = view.keyLayout
        val filterKeyTextView: TextView = view.filterKeyTextView
        val relationLayout: RelativeLayout = view.relationLayout
        val filterRelationTextView: TextView = view.filterRelationTextView
        val hoursAgoLayout: RelativeLayout = view.hoursAgoLayout
        val filterHoursAgoTextView: TextView = view.filterHoursAgoTextView
        val valueLayout: RelativeLayout = view.valueLayout
        val filterValueTextView: TextView = view.filterValueTextView
        val radiusLayout: RelativeLayout = view.radiusLayout
        val filterRadiusTextView: TextView = view.filterRadiusTextView
        val latitudeLayout: RelativeLayout = view.latitudeLayout
        val filterLatitudeTextView: TextView = view.filterLatitudeTextView
        val longitudeLayout: RelativeLayout = view.longitudeLayout
        val filterLongitudeTextView: TextView = view.filterLongitudeTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.filter_item_layout,
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
        val currentFilter = filters[position]

        if (currentFilter.operator.isNullOrBlank()) {
            holder.operatorLayout.visibility = GONE
        } else {
            holder.operatorLayout.visibility = VISIBLE
            holder.filterOperatorTextView.text = currentFilter.operator
        }

        if (currentFilter.field.isNullOrBlank()) {
            holder.fieldLayout.visibility = GONE
        } else {
            holder.fieldLayout.visibility = VISIBLE
            holder.filterFieldTextView.text = currentFilter.field
        }

        if (currentFilter.hoursAgo.isNullOrBlank()) {
            holder.hoursAgoLayout.visibility = GONE
        } else {
            holder.hoursAgoLayout.visibility = VISIBLE
            holder.filterHoursAgoTextView.text = currentFilter.hoursAgo
        }

        if (currentFilter.key.isNullOrBlank()) {
            holder.keyLayout.visibility = GONE
        } else {
            holder.keyLayout.visibility = VISIBLE
            holder.filterKeyTextView.text = currentFilter.key
        }

        if (currentFilter.latitude.isNullOrBlank()) {
            holder.latitudeLayout.visibility = GONE
        } else {
            holder.latitudeLayout.visibility = VISIBLE
            holder.filterLatitudeTextView.text = currentFilter.latitude
        }

        if (currentFilter.longitude.isNullOrBlank()) {
            holder.longitudeLayout.visibility = GONE
        } else {
            holder.longitudeLayout.visibility = VISIBLE
            holder.filterLongitudeTextView.text = currentFilter.longitude
        }

        if (currentFilter.radius.isNullOrBlank()) {
            holder.radiusLayout.visibility = GONE
        } else {
            holder.radiusLayout.visibility = VISIBLE
            holder.filterRadiusTextView.text = currentFilter.radius
        }

        if (currentFilter.relation.isNullOrBlank()) {
            holder.relationLayout.visibility = GONE
        } else {
            holder.relationLayout.visibility = VISIBLE
            holder.filterRelationTextView.text = currentFilter.relation
        }

        if (currentFilter.value.isNullOrBlank()) {
            holder.valueLayout.visibility = GONE
        } else {
            holder.valueLayout.visibility = VISIBLE
            holder.filterValueTextView.text = currentFilter.value
        }

        holder.filterNumberTextView.text = "Filter ${position + 1}"

        if ((filters.size - 1) == position) {
            holder.deleteFilterButton.visibility = VISIBLE
        } else {
            holder.deleteFilterButton.visibility = GONE
        }

        holder.deleteFilterButton.setOnClickListener {
            deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }

}