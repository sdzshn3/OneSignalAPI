package com.sdzshn3.onesignalapi.ui.notificationHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.databinding.NotificationItemLayoutBinding
import com.sdzshn3.onesignalapi.model.Notification
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationHistoryAdapter : RecyclerView.Adapter<NotificationHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: NotificationItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

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
            NotificationItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            notifications[position].let {
                sentTV.text = "Sent: ${it.successful}"
                notificationContentTV.text = it.contents.en
                val timeInMillis = it.queuedAt*1000
                val date = Date(timeInMillis)
                val dateString = SimpleDateFormat("d MMM yyyy, h:mma", Locale.getDefault()).format(date)
                notificationDate.text = dateString
            }
            root.setOnClickListener {
                onItemCLickListener?.let {
                    it(notifications[holder.adapterPosition])
                }
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