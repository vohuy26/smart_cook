package com.example.smart_cook.ui.admin.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_cook.databinding.ItemReviewAdminBinding
import com.example.smart_cook.model.Review

class ReviewAdapter(

    private val reviewList: List<Review>,

    private val onDelete: (Review) -> Unit

) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemReviewAdminBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemReviewAdminBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val review =
            reviewList[position]

        holder.binding.txtUser.text =
            review.userName

        holder.binding.txtRecipe.text =
            review.recipeName

        holder.binding.txtRating.text =
            "⭐ ${review.rating}"

        holder.binding.txtComment.text =
            review.comment

        holder.binding.btnDeleteReview.setOnClickListener {

            onDelete(review)
        }
    }

    override fun getItemCount(): Int {

        return reviewList.size
    }
}