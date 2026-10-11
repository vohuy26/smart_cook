package com.example.smart_cook.ui.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_cook.databinding.ItemAdminLogBinding
import com.example.smart_cook.model.ActivityLog

class AdminLogAdapter(
    private val list: List<ActivityLog>
) : RecyclerView.Adapter<AdminLogAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemAdminLogBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemAdminLogBinding.inflate(
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

        holder.binding.txtLog.text =
            list[position].title
    }

    override fun getItemCount() =
        list.size
}