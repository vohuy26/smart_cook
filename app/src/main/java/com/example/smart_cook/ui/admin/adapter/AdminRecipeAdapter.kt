package com.example.smart_cook.ui.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.smart_cook.databinding.ItemRecipeAdminBinding
import com.example.smart_cook.ui.admin.model.AdminRecipe


class AdminRecipeAdapter(

    private val recipeList:
    List<AdminRecipe>,

    private val onDelete:
        (AdminRecipe) -> Unit,

    private val onEdit:
        (AdminRecipe) -> Unit,

    private val onView:
        (AdminRecipe) -> Unit,

    private val onApprove:
        (AdminRecipe) -> Unit,

    private val onReject:
        (AdminRecipe) -> Unit

) : RecyclerView.Adapter<
        AdminRecipeAdapter.ViewHolder>() {


    inner class ViewHolder(
        val binding:
        ItemRecipeAdminBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemRecipeAdminBinding.inflate(
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
            recipeList[position]


        holder.binding
            .txtRecipeName.text =
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


        // =========================
        // OWNER
        // =========================

        if (
            recipe.creatorType == "USER"
        ) {

            holder.binding
                .txtOwner.visibility =
                View.VISIBLE

            holder.binding
                .txtOwner.text =
                "Người gửi: ${
                    recipe.ownerName
                        .ifBlank {
                            "User"
                        }
                }"

        } else {

            holder.binding
                .txtOwner.visibility =
                View.VISIBLE

            holder.binding
                .txtOwner.text =
                "Nguồn: Admin"
        }


        // =========================
        // STATUS
        // =========================

        when (
            recipe.status.uppercase()
        ) {

            "PENDING" -> {

                holder.binding
                    .txtStatus.text =
                    "Chờ duyệt"

                holder.binding
                    .txtStatus.setTextColor(
                        0xFFFF9800.toInt()
                    )
            }


            "REJECTED" -> {

                holder.binding
                    .txtStatus.text =
                    "Đã từ chối"

                holder.binding
                    .txtStatus.setTextColor(
                        0xFFF44336.toInt()
                    )
            }


            else -> {

                holder.binding
                    .txtStatus.text =
                    "Đã duyệt"

                holder.binding
                    .txtStatus.setTextColor(
                        0xFF4CAF50.toInt()
                    )
            }
        }


        // =========================
        // BUTTON VISIBILITY
        // =========================

        val isPendingUserRecipe =
            recipe.creatorType == "USER" &&
                    recipe.status ==
                    "PENDING"


        if (
            isPendingUserRecipe
        ) {

            // User đang chờ duyệt
            holder.binding
                .btnApprove.visibility =
                View.VISIBLE

            holder.binding
                .btnReject.visibility =
                View.VISIBLE

            holder.binding
                .btnEdit.visibility =
                View.GONE

            holder.binding
                .btnDelete.visibility =
                View.GONE

        } else {

            // Admin recipe hoặc recipe đã xử lý
            holder.binding
                .btnApprove.visibility =
                View.GONE

            holder.binding
                .btnReject.visibility =
                View.GONE

            holder.binding
                .btnEdit.visibility =
                View.VISIBLE

            holder.binding
                .btnDelete.visibility =
                View.VISIBLE
        }


        // =========================
        // ACTIONS
        // =========================

        holder.binding
            .btnView
            .setOnClickListener {

                onView(
                    recipe
                )
            }


        holder.binding
            .btnApprove
            .setOnClickListener {

                onApprove(
                    recipe
                )
            }


        holder.binding
            .btnReject
            .setOnClickListener {

                onReject(
                    recipe
                )
            }


        holder.binding
            .btnEdit
            .setOnClickListener {

                onEdit(
                    recipe
                )
            }


        holder.binding
            .btnDelete
            .setOnClickListener {

                onDelete(
                    recipe
                )
            }
    }


    override fun getItemCount():
            Int {

        return recipeList.size
    }
}