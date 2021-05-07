package com.sdzshn3.onesignalapi.ui.newPush.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.onesignalapi.databinding.IosMediaItemLayoutBinding
import com.sdzshn3.onesignalapi.interfaces.DeleteButtonListener
import com.sdzshn3.onesignalapi.model.Media

@SuppressLint("SetTextI18n")
class MediaAdapter(
    private val context: Context,
    private var medias: List<Media>
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    private var deleteButtonListener: DeleteButtonListener? = null

    class ViewHolder(val binding: IosMediaItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            IosMediaItemLayoutBinding.inflate(
                LayoutInflater.from(context),
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
        holder.binding.apply {
            mediaNumberTextView.text = "MEDIA " + medias[position].number.toString()
            keyTextView.text = medias[position].key
            valueTextView.text = medias[position].value

            deleteMediaButton.setOnClickListener {
                deleteButtonListener?.onDeleteButtonPressed(holder.layoutPosition)
            }
        }
    }

    fun setDeleteButtonListener(deleteButtonListener: DeleteButtonListener) {
        this.deleteButtonListener = deleteButtonListener
    }
}