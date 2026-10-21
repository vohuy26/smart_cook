package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.example.smart_cook.databinding.ItemRecipeCommentBinding
import com.example.smart_cook.model.Review

import com.google.firebase.auth.FirebaseAuth

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RecipeCommentAdapter :

    RecyclerView.Adapter<
            RecipeCommentAdapter.CommentViewHolder
            >() {


    private val items =
        mutableListOf<Review>()


    private val currentUserId: String?
        get() =
            FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid


    inner class CommentViewHolder(
        val binding:
        ItemRecipeCommentBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {

        val binding =
            ItemRecipeCommentBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return CommentViewHolder(
            binding
        )
    }


    override fun onBindViewHolder(
        holder: CommentViewHolder,
        position: Int
    ) {

        val review =
            items[position]


        val displayName =
            if (
                review.userId ==
                currentUserId
            ) {

                "Bạn"

            } else {

                review.userName
                    .ifBlank {
                        "Người dùng"
                    }
            }


        holder.binding
            .txtCommentUser
            .text =
            displayName


        holder.binding
            .txtCommentContent
            .text =
            review.comment


        val time =
            if (
                review.updatedAt > 0L
            ) {

                review.updatedAt

            } else {

                review.createdAt
            }


        holder.binding
            .txtCommentTime
            .text =
            formatTime(
                time
            )
    }


    override fun getItemCount():
            Int {

        return items.size
    }


    fun submitList(
        newItems: List<Review>
    ) {

        items.clear()

        items.addAll(
            newItems
        )

        notifyDataSetChanged()
    }


    private fun formatTime(
        time: Long
    ): String {

        if (time <= 0L) {
            return ""
        }


        val formatter =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            )


        return formatter.format(
            Date(time)
        )
    }
}