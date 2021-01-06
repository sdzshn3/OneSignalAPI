package com.sdzshn3.onesignalapi.ui.notificationHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.model.Notification
import kotlinx.android.synthetic.main.notification_item_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationHistoryAdapter : RecyclerView.Adapter<NotificationHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private val notifications = ArrayList<Notification>()
    private var onItemCLickListener: ((Notification) -> Unit)? = null

    fun submitList(_notifications: List<Notification>) {
        println(_notifications)
        notifications.clear()
        notifications.addAll(_notifications)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.notification_item_layout,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        notifications[position].let {
            holder.view.sentTV.text = "Sent: ${it.successful}"
            holder.view.notificationContentTV.text = it.contents.en
            val timeInMillis = it.queuedAt*1000
            val date = Date(timeInMillis)
            val dateString = SimpleDateFormat("d MMM yyyy, h:mma", Locale.getDefault()).format(date)
            holder.view.notificationDate.text = dateString
        }

        holder.view.setOnClickListener {
            onItemCLickListener?.let {
                it(notifications[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun setOnItemClickLister(onItemClick: (Notification) -> Unit) {
        onItemCLickListener = onItemClick
    }
}