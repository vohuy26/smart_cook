package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_cook.databinding.ItemOnboardingBinding
import com.example.smart_cook.model.OnboardingItem

class OnboardingAdapter(
    private val onboardingList: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(
        val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnboardingViewHolder {

        val binding =
            ItemOnboardingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OnboardingViewHolder,
        position: Int
    ) {

        val item =
            onboardingList[position]

        holder.binding.imgOnboarding
            .setImageResource(item.image)

        holder.binding.txtTitle.text =
            item.title

        holder.binding.txtDescription.text =
            item.description
    }

    override fun getItemCount(): Int {

        return onboardingList.size
    }
}