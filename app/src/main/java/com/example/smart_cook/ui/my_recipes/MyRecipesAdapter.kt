package com.example.smart_cook.ui.my_recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import com.example.smart_cook.databinding.ItemMyRecipeBinding
import com.example.smart_cook.model.UserRecipe


class MyRecipesAdapter(

    private var recipes:
    List<UserRecipe> =
        emptyList()

) : RecyclerView.Adapter<
        MyRecipesAdapter.ViewHolder>() {


    inner class ViewHolder(
        val binding:
        ItemMyRecipeBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemMyRecipeBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return ViewHolder(
            binding
        )
    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val recipe =
            recipes[position]


        holder.binding
            .txtRecipeTitle.text =
            recipe.title


        holder.binding
            .txtCategory.text =
            recipe.category


        Glide.with(
            holder.itemView.context
        )
            .load(
                recipe.image
            )
            .placeholder(
                android.R.drawable.ic_menu_gallery
            )
            .into(
                holder.binding.imgRecipe
            )


        when (
            recipe.status.uppercase()
        ) {

            "PENDING" -> {

                holder.binding
                    .txtStatus.text =
                    "Chờ duyệt"

                holder.binding
                    .txtStatus
                    .setTextColor(
                        0xFFFF9800.toInt()
                    )

                holder.binding
                    .txtRejectionReason
                    .visibility =
                    View.GONE
            }


            "REJECTED" -> {

                holder.binding
                    .txtStatus.text =
                    "Đã từ chối"

                holder.binding
                    .txtStatus
                    .setTextColor(
                        0xFFF44336.toInt()
                    )


                holder.binding
                    .txtRejectionReason
                    .visibility =
                    View.VISIBLE


                holder.binding
                    .txtRejectionReason
                    .text =
                    "Lý do: ${
                        recipe.rejectionReason
                            .ifBlank {
                                "Không có lý do."
                            }
                    }"
            }


            else -> {

                holder.binding
                    .txtStatus.text =
                    "Đã duyệt"

                holder.binding
                    .txtStatus
                    .setTextColor(
                        0xFF4CAF50.toInt()
                    )

                holder.binding
                    .txtRejectionReason
                    .visibility =
                    View.GONE
            }
        }
    }


    override fun getItemCount():
            Int {

        return recipes.size
    }


    fun submitList(
        newRecipes:
        List<UserRecipe>
    ) {

        recipes =
            newRecipes

        notifyDataSetChanged()
    }
}