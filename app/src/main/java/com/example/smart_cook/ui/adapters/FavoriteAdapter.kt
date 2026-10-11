package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smart_cook.databinding.ItemFavoriteBinding
import com.example.smart_cook.model.FavoriteRecipe

class FavoriteAdapter(

    list: List<FavoriteRecipe>,

    private val onDelete: (FavoriteRecipe) -> Unit,

    private val onItemClick: (FavoriteRecipe) -> Unit

) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    // Adapter giữ một bản sao của danh sách
    private val list = list.toMutableList()

    inner class ViewHolder(
        val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemFavoriteBinding.inflate(
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

        val item = list[position]

        holder.binding.txtTitle.text = item.title

        Glide.with(holder.itemView.context)
            .load(item.image)
            .centerCrop()
            .into(holder.binding.imgRecipe)

        holder.binding.btnDelete.setOnClickListener {
            onDelete(item)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<FavoriteRecipe>) {

        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()

    }

    fun getItem(position: Int): FavoriteRecipe {
        return list[position]
    }
}