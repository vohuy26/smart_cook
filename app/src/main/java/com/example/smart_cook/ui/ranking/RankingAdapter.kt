package com.example.smart_cook.ui.ranking

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.example.smart_cook.databinding.ItemRecipeRankingBinding
import com.example.smart_cook.model.RecipeRanking

import java.util.Locale


class RankingAdapter(
    private val onItemClick: (RecipeRanking) -> Unit
) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {


    private val items =
        mutableListOf<RecipeRanking>()


    inner class RankingViewHolder(
        val binding: ItemRecipeRankingBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RankingViewHolder {

        val binding =
            ItemRecipeRankingBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return RankingViewHolder(
            binding
        )
    }


    override fun onBindViewHolder(
        holder: RankingViewHolder,
        position: Int
    ) {

        val item =
            items[position]


        holder.binding.txtRank.text =
            when (position) {

                0 -> "🥇"

                1 -> "🥈"

                2 -> "🥉"

                else ->
                    "#${position + 1}"
            }


        holder.binding.txtRecipeName.text =
            item.recipeName


        holder.binding.txtRatingInfo.text =
            String.format(
                Locale.getDefault(),
                "⭐ %.1f · %d đánh giá",
                item.averageRating,
                item.ratingCount
            )


        holder.binding.root
            .setOnClickListener {

                onItemClick(
                    item
                )
            }
    }


    override fun getItemCount():
            Int {

        return items.size
    }


    fun submitList(
        newItems: List<RecipeRanking>
    ) {

        items.clear()

        items.addAll(
            newItems
        )

        notifyDataSetChanged()
    }
}