package com.example.smart_cook.ui.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smart_cook.R
import com.example.smart_cook.databinding.ItemAdminUserBinding
import com.example.smart_cook.model.User

class AdminUserAdapter(

    private val userList: List<User>,

    private val onAdminClick: (User) -> Unit

) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemAdminUserBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemAdminUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val user = userList[position]

        with(holder.binding) {

            txtName.text =
                if (user.name.isBlank())
                    "Unknown User"
                else
                    user.name

            txtEmail.text = user.email

            txtRole.text =
                if (user.role == "admin")
                    "Admin 👑"
                else
                    "User"

            if (user.photoUrl.isNotBlank()) {

                imgAvatar.visibility = View.VISIBLE

                txtAvatar.visibility = View.GONE

                Glide.with(root)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_chef)
                    .circleCrop()
                    .into(imgAvatar)

            } else {

                imgAvatar.visibility = View.GONE

                txtAvatar.visibility = View.VISIBLE

                txtAvatar.text =
                    user.name
                        .firstOrNull()
                        ?.uppercase()
                        ?: "U"
            }

            if (user.role == "admin") {

                btnMakeAdmin.setImageResource(
                    R.drawable.ic_verified
                )

                btnMakeAdmin.isEnabled = false

            } else {

                btnMakeAdmin.setImageResource(
                    R.drawable.ic_chef
                )

                btnMakeAdmin.isEnabled = true

                btnMakeAdmin.setOnClickListener {

                    onAdminClick(user)
                }
            }
        }
    }
}