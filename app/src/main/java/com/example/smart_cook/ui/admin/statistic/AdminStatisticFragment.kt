package com.example.smart_cook.ui.admin.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smart_cook.databinding.FragmentAdminStatisticBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminStatisticFragment : Fragment() {

    private var _binding: FragmentAdminStatisticBinding? = null
    private val binding get() = _binding!!

    private val firestore =
        FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAdminStatisticBinding.inflate(
                inflater,
                container,
                false
            )

        loadStatistics()

        return binding.root
    }

    private fun loadStatistics() {

        loadUsers()

        loadRecipes()

        loadReviews()

        loadFavorites()

        loadTopRecipe()
    }

    private fun loadUsers() {

        firestore.collection("users")
            .get()
            .addOnSuccessListener {

                binding.txtTotalUsers.text =
                    it.size().toString()
            }
            .addOnFailureListener {

                showError(it.message)
            }
    }

    private fun loadRecipes() {

        firestore.collection("recipes")
            .get()
            .addOnSuccessListener {

                binding.txtTotalRecipes.text =
                    it.size().toString()
            }
            .addOnFailureListener {

                showError(it.message)
            }
    }

    private fun loadReviews() {

        firestore.collection("reviews")
            .get()
            .addOnSuccessListener {

                binding.txtTotalReviews.text =
                    it.size().toString()
            }
            .addOnFailureListener {

                showError(it.message)
            }
    }

    private fun loadFavorites() {

        firestore.collection("recipes")
            .get()
            .addOnSuccessListener { result ->

                var totalFavorites = 0L

                result.documents.forEach { doc ->

                    totalFavorites +=
                        doc.getLong("favoriteCount")
                            ?: 0
                }

                binding.txtTotalFavorites.text =
                    totalFavorites.toString()
            }
            .addOnFailureListener {

                showError(it.message)
            }
    }

    private fun loadTopRecipe() {

        firestore.collection("recipes")
            .orderBy(
                "favoriteCount",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            .limit(1)
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {

                    binding.txtMostLikedRecipe.text =
                        "Chưa có dữ liệu"

                    return@addOnSuccessListener
                }

                val recipe =
                    result.documents.first()

                val title =
                    recipe.getString("title")
                        ?: "Unknown"

                val count =
                    recipe.getLong("favoriteCount")
                        ?: 0

                binding.txtMostLikedRecipe.text =
                    "$title ❤️ ($count lượt thích)"
            }
            .addOnFailureListener {

                showError(it.message)
            }
    }

    private fun showTopRecipe(
        favoriteCount: Map<String, Int>,
        titleMap: Map<String, String>
    ) {

        if (favoriteCount.isEmpty()) {

            binding.txtMostLikedRecipe.text =
                "Chưa có lượt thích"

            return
        }

        val topMealId =
            favoriteCount.maxByOrNull {
                it.value
            }?.key

        val topTitle =
            titleMap[topMealId]
                ?: "Unknown"

        val totalLike =
            favoriteCount[topMealId]
                ?: 0

        binding.txtMostLikedRecipe.text =
            "$topTitle ($totalLike ❤️)"
    }

    private fun showError(
        message: String?
    ) {

        Toast.makeText(
            requireContext(),
            message ?: "Unknown Error",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}