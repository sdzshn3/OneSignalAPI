package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.model.Media
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import kotlinx.android.synthetic.main.ios_media_item_layout.view.*

@SuppressLint("SetTextI18n")
class MediaAdapter(
    private val context: Context,
    private var medias: List<Media>
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mediaNumberTextView: TextView = view.mediaNumberTextView
        val deleteFieldButton: TextView = view.deleteMediaButton
        val keyEditText: TextView = view.keyTextView
        val valueEditText: TextView = view.valueTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.ios_media_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return medias.size
    }

    fun submitList(mediaList: List<Media>) {
        medias = mediaList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mediaNumberTextView.text = "MEDIA " + medias[position].number.toString()
        holder.keyEditText.text = medias[position].key
        holder.valueEditText.text = medias[position].value

        holder.deleteFieldButton.setOnClickListener {
            deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}