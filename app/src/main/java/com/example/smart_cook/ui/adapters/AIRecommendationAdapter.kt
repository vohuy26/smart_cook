package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smart_cook.databinding.ItemRecommendationBinding
import com.example.smart_cook.model.AIRecommendation

class AIRecommendationAdapter(
    private val list: List<AIRecommendation>
) : RecyclerView.Adapter<AIRecommendationAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemRecommendationBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemRecommendationBinding.inflate(
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

        val item =
            list[position]

        // TITLE

        holder.binding.txtRecommendationTitle.text =
            item.title

        // IMAGE

        Glide.with(holder.itemView.context)
            .load(item.image)
            .centerCrop()
            .into(holder.binding.imgRecommendation)
    }

    override fun getItemCount(): Int {

        return list.size
    }
}