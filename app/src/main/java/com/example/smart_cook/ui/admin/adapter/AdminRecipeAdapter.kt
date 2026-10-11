package com.example.smart_cook.ui.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smart_cook.databinding.ItemRecipeAdminBinding
import com.example.smart_cook.ui.admin.model.AdminRecipe

class AdminRecipeAdapter(

    private val recipeList: List<AdminRecipe>,

    private val onDelete: (AdminRecipe) -> Unit,

    private val onEdit: (AdminRecipe) -> Unit

) : RecyclerView.Adapter<AdminRecipeAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemRecipeAdminBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemRecipeAdminBinding.inflate(
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

        val recipe = recipeList[position]

        holder.binding.txtRecipeName.text =
            recipe.title

        holder.binding.txtCategory.text =
            recipe.category

        Glide.with(holder.itemView.context)
            .load(recipe.image)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.binding.imgRecipe)

        holder.binding.txtStatus.text =
            if (recipe.source == "firebase")
                "Firebase"
            else
                "API"

        holder.binding.btnEdit.setOnClickListener {
            onEdit(recipe)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDelete(recipe)
        }
    }

    override fun getItemCount(): Int {

        return recipeList.size
    }
}