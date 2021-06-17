package com.example.monuments.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monuments.R

class ImagesAdapter( private val listener: ImagesListener):
    ListAdapter<Uri, RecyclerView.ViewHolder>(DiffUtilCallBack()) {

    private val viewType1 = 1
    private val viewType2 = 2

    interface ImagesListener {
        fun onDeleteClick(position: Int)
        fun onAddClick()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewType1 -> {
                ImagesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_image, parent, false))
            } else -> {
                AddButtonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_add_btn, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            viewType1 -> {
                val imagesViewHolder = holder as ImagesViewHolder
                imagesViewHolder.bind(getItem(position))
            } else -> {
                val addButtonViewHolder = holder as AddButtonViewHolder
                addButtonViewHolder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position != currentList.size) {
            viewType1

        } else {
            viewType2
        }
    }

    override fun getItemCount(): Int = currentList.size+1

    inner class ImagesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.row_image__img__monument_image)
        private val deleteBtn: ImageButton = view.findViewById(R.id.row_image__btn__delete_btn)
        fun bind (item: Uri) {
            Glide.with(itemView.context).load(item).into(image)
            deleteBtn.setOnClickListener {
                listener.onDeleteClick(adapterPosition)
            }
        }
    }

    inner class AddButtonViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val addButton: ImageButton = view.findViewById(R.id.row_add_monument__btn__add_image)
        fun bind () {
            addButton.setOnClickListener {
                listener.onAddClick()
            }
        }
    }
}

    private class DiffUtilCallBack: DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem.path == newItem.path


    }