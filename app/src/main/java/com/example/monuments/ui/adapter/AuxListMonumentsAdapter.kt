package com.example.monuments.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monuments.R
import com.example.monuments.domain.MonumentBO

class AuxListMonumentsAdapter(private val listener: AuxMonumentsListener):
    ListAdapter<MonumentBO, AuxListMonumentsAdapter.ViewHolder>(DiffUtilAuxListMonuments()){

    interface AuxMonumentsListener {
        fun onClick(id: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_aux_list_monuments, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardView)
        val title: TextView = view.findViewById(R.id.cardView_favorites__label__title)
        val description : TextView = view.findViewById(R.id.cardView_favorites__label__description)
        private val image: ImageView = view.findViewById(R.id.cardView_favorites__img__image)
        fun bind(item: MonumentBO){
            title.text = item.name
            description.text = item.description
            Glide.with(itemView.context).load(item.images[0].url).into(image)
            cardView.setOnClickListener {
                listener.onClick(item.id)
            }
        }
    }
}

private class DiffUtilAuxListMonuments: DiffUtil.ItemCallback<MonumentBO>() {
    override fun areItemsTheSame(oldItem: MonumentBO, newItem: MonumentBO): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MonumentBO, newItem: MonumentBO): Boolean = oldItem.favorite.isFavorite == newItem.favorite.isFavorite
}