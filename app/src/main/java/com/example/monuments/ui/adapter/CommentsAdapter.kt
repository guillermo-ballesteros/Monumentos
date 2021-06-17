package com.example.monuments.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.monuments.R
import com.example.monuments.domain.CommentBO
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.dialog.CommentDialogFragment

class CommentsAdapter(private val commentsInterface: CommentsInterface):
    ListAdapter<CommentBO, RecyclerView.ViewHolder>(CommentDiffUtil()) {

    private val viewType1 = 1
    private val viewType2 = 2

    interface CommentsInterface {
        fun getEmail(): String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewType1 -> {
                FirstTypeViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.first_type_comment_row, parent, false)
                )
            } else -> {
                SecondTypeViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.second_type_comment_row, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            viewType1 -> {
                val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                val firstTypeViewHolder = holder as CommentsAdapter.FirstTypeViewHolder
                firstTypeViewHolder.bind(getItem(position))
            } else -> {

                val secondTypeViewHolder = holder as CommentsAdapter.SecondTypeViewHolder
                secondTypeViewHolder.bind(getItem(position))

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).user == commentsInterface.getEmail()) {
            viewType2

        } else {
            viewType1
        }
    }


    inner class FirstTypeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val userLabelView: TextView = view.findViewById(R.id.type_1__label__user)
        private val commentLabelView: TextView = view.findViewById(R.id.type_1__label__comment)
        private val ratingBarView: RatingBar = view.findViewById(R.id.type_1__rating__rating_bar)
        fun bind(item: CommentBO) {
            userLabelView.text = item.user
            commentLabelView.text = item.comment
            ratingBarView.rating = item.rate.toFloat()
        }
    }

    inner class SecondTypeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val user: TextView = view.findViewById(R.id.type_2__label__user)
        private val comment: TextView = view.findViewById(R.id.type_2__label__comment)
        private val ratingBarView: RatingBar = view.findViewById(R.id.type_2__rating__rating_bar)
        fun bind(item: CommentBO) {
            user.text = item.user
            comment.text = item.comment
            ratingBarView.rating = item.rate.toFloat()
        }
    }

}
private class CommentDiffUtil(): DiffUtil.ItemCallback<CommentBO>() {
    override fun areItemsTheSame(oldItem: CommentBO, newItem: CommentBO): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CommentBO, newItem: CommentBO): Boolean {
        return (oldItem.rate == newItem.rate && oldItem.comment == newItem.comment)
    }

}