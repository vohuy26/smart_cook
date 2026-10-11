package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import com.example.smart_cook.R
import com.example.smart_cook.model.RecentlyViewed

class RecentlyViewedAdapter(

    private val list: List<RecentlyViewed>,

    private val onItemClick: (RecentlyViewed) -> Unit

) : RecyclerView.Adapter<RecentlyViewedAdapter.ViewHolder>() {

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val imgFood: ImageView =
            itemView.findViewById(
                R.id.imgFood
            )

        val txtTitle: TextView =
            itemView.findViewById(
                R.id.txtTitle
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =

            LayoutInflater
                .from(parent.context)

                .inflate(
                    R.layout.item_recently_viewed,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.txtTitle.text =
            item.title

        Glide.with(holder.itemView.context)
            .load(item.image)
            .placeholder(
                R.drawable.ic_launcher_foreground
            )
            .error(
                R.drawable.ic_launcher_foreground
            )
            .centerCrop()
            .into(holder.imgFood)

        holder.itemView.setOnClickListener {

            onItemClick(item)

        }
    }
}