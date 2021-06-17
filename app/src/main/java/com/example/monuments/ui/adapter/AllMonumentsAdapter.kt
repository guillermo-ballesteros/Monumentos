package com.example.monuments.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monuments.R
import com.example.monuments.domain.MonumentBO

class AllMonumentsAdapter( private val listener: AllMonumentsListener):
    ListAdapter<MonumentBO, AllMonumentsAdapter.ViewHolder>(DiffUtilAllMonuments()){

    interface AllMonumentsListener {
        fun onFavClick(position: Int)
        fun onLongClick(position: Int)
        fun onClick(id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_monument, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val cardView: CardView = view.findViewById(R.id.cardView)

        private val title: TextView = view.findViewById(R.id.cardView__label__title)

        private val city: TextView = view.findViewById(R.id.cardView__label__city)

        private val image: ImageView = view.findViewById(R.id.cardView__img__image)

        private val favBtn: ImageButton = view.findViewById(R.id.cardView__btn__fav)

        init {
            cardView.setOnClickListener {
                listener.onClick(getItem(adapterPosition).id)
            }

            cardView.setOnLongClickListener {
                listener.onLongClick(adapterPosition)
                true
            }

            favBtn.setOnClickListener {
                listener.onFavClick(adapterPosition)
            }
        }
        fun bind(item: MonumentBO) {
            title.text = item.name
            city.text = item.city

            Glide.with(itemView.context).load(item.images[0].url).into(image)

            if (item.favorite.isFavorite) {
                favBtn.setBackgroundResource(R.drawable.ic_fav_on)

            } else {
                favBtn.setBackgroundResource(R.drawable.ic_fav_off)
            }

        }

    }
}

    private class DiffUtilAllMonuments: DiffUtil.ItemCallback<MonumentBO>() {
        override fun areItemsTheSame(oldItem: MonumentBO, newItem: MonumentBO): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MonumentBO, newItem: MonumentBO): Boolean = oldItem.favorite.isFavorite == newItem.favorite.isFavorite
    }