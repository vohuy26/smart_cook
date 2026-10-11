package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_cook.databinding.ItemCategoryBinding
import com.example.smart_cook.model.Category

class CategoryAdapter(

    private val categoryList: List<Category>,

    private val onClick: (Category) -> Unit

) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    private var selectedPosition = 0


    inner class CategoryViewHolder(
        val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {


        val binding =
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )


        return CategoryViewHolder(binding)
    }



    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {


        val category =
            categoryList[position]


        holder.binding.txtCategory.text =
            category.name



        if(selectedPosition == position){


            holder.binding.root.setCardBackgroundColor(
                android.graphics.Color.parseColor("#7C4DFF")
            )


            holder.binding.txtCategory.setTextColor(
                android.graphics.Color.WHITE
            )


        }else{


            holder.binding.root.setCardBackgroundColor(
                android.graphics.Color.parseColor("#F3EEFF")
            )


            holder.binding.txtCategory.setTextColor(
                android.graphics.Color.parseColor("#7C4DFF")
            )
        }



        holder.itemView.setOnClickListener {


            val old =
                selectedPosition


            selectedPosition =
                position


            notifyItemChanged(old)
            notifyItemChanged(position)


            onClick(category)

        }

    }



    override fun getItemCount(): Int {

        return categoryList.size

    }

}