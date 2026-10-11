package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smart_cook.R
import com.example.smart_cook.databinding.ItemRecipeBinding
import com.example.smart_cook.model.Meal

class RecipeAdapter(

    private var recipeList: List<Meal>,

    private val favoriteIds: MutableSet<String>,

    private val onItemClick: (Meal) -> Unit,

    private val onFavoriteClick: (Meal) -> Unit

) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(
        val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {

        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecipeViewHolder,
        position: Int
    ) {

        val recipe = recipeList[position]

        holder.binding.txtTitle.text = recipe.title

        Glide.with(holder.itemView.context)
            .load(recipe.image)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .centerCrop()
            .into(holder.binding.imgRecipe)

        updateFavoriteIcon(holder, recipe.id)

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }

        holder.binding.imgFavorite.setOnClickListener {
            onFavoriteClick(recipe)
        }
    }

    private fun updateFavoriteIcon(
        holder: RecipeViewHolder,
        id: String
    ) {

        if (favoriteIds.contains(id)) {

            holder.binding.imgFavorite.setImageResource(
                android.R.drawable.btn_star_big_on
            )

        } else {

            holder.binding.imgFavorite.setImageResource(
                R.drawable.ic_favorite
            )
        }
    }

    fun updateData(newList: List<Meal>) {
        recipeList = newList
        notifyDataSetChanged()
    }

    fun setFavoriteIds(ids: Set<String>) {
        favoriteIds.clear()
        favoriteIds.addAll(ids)
        notifyDataSetChanged()
    }

    fun addFavorite(id: String) {

        if (!favoriteIds.contains(id)) {

            favoriteIds.add(id)
        }
    }

    fun removeFavorite(id: String) {

        favoriteIds.remove(id)
    }

    fun isFavorite(id: String): Boolean {
        return favoriteIds.contains(id)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}