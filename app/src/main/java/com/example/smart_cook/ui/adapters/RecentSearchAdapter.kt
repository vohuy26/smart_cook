package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_cook.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(
    private val searchList: MutableList<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemRecentSearchBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemRecentSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val keyword =
            searchList[position]

        holder.binding.txtKeyword.text =
            keyword

        holder.binding.root.setOnClickListener {

            onClick(keyword)
        }
    }

    fun updateList(
        newList: List<String>
    ) {

        searchList.clear()

        searchList.addAll(
            newList.toMutableList()
        )

        notifyDataSetChanged()

    }
}