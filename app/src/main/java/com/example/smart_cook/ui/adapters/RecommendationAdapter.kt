package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smart_cook.databinding.ItemRecommendationBinding
import com.example.smart_cook.model.Recommendation

class RecommendationAdapter(
    private val recommendationList: List<Recommendation>
) : RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {

    inner class RecommendationViewHolder(
        val binding: ItemRecommendationBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationViewHolder {

        val binding =
            ItemRecommendationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return RecommendationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecommendationViewHolder,
        position: Int
    ) {

        val recommendation =
            recommendationList[position]

        holder.binding.txtRecommendationTitle.text =
            recommendation.title

        Glide.with(holder.itemView.context)
            .load(recommendation.image)
            .into(holder.binding.imgRecommendation)
    }

    override fun getItemCount(): Int {

        return recommendationList.size
    }
}