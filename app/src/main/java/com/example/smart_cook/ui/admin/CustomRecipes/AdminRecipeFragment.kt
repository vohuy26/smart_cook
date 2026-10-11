package com.example.smart_cook.ui.admin.CustomRecipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.Query
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.addTextChangedListener
import com.example.smart_cook.R
import com.example.smart_cook.databinding.FragmentAdminRecipeBinding
import com.example.smart_cook.ui.admin.adapter.AdminRecipeAdapter
import com.example.smart_cook.ui.admin.model.AdminRecipe
import com.google.firebase.firestore.FirebaseFirestore

class AdminRecipeFragment : Fragment() {

    private var _binding: FragmentAdminRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdminRecipeAdapter

    private val recipeList =
        mutableListOf<AdminRecipe>()

    private val allRecipes =
        mutableListOf<AdminRecipe>()

    private val firestore =
        FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAdminRecipeBinding.inflate(
                inflater,
                container,
                false
            )

        setupRecycler()

        setupCategoryFilter()

        setupSearch()

        setupAddRecipe()

        loadRecipes()

        return binding.root
    }

    private fun setupRecycler() {

        adapter =
            AdminRecipeAdapter(

                recipeList,

                onDelete = { recipe ->

                    AlertDialog.Builder(requireContext())
                        .setTitle("Xóa món ăn")
                        .setMessage(
                            "Bạn có chắc muốn xóa \"${recipe.title}\" không?"
                        )
                        .setPositiveButton("Xóa") { _, _ ->

                            deleteRecipe(recipe.id)
                        }
                        .setNegativeButton("Hủy", null)
                        .show()
                },

                onEdit = { recipe ->

                    val intent =
                        Intent(
                            requireContext(),
                            EditRecipeActivity::class.java
                        )

                    intent.putExtra(
                        "recipeId",
                        recipe.id
                    )

                    startActivity(intent)
                }
            )

        binding.rvRecipes.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvRecipes.adapter =
            adapter
    }

    private fun setupAddRecipe() {

        binding.btnAddRecipe.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    AddRecipeActivity::class.java
                )
            )
        }
    }

    private fun setupSearch() {

        binding.edtSearchRecipe
            .addTextChangedListener {

                filterRecipe()
            }
    }

    private fun setupCategoryFilter() {

        val adapterSpinner =
            ArrayAdapter.createFromResource(

                requireContext(),

                R.array.recipe_categories_admin,

                android.R.layout.simple_spinner_item
            )

        adapterSpinner.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spCategory.adapter =
            adapterSpinner

        binding.spCategory.onItemSelectedListener =
            object :
                AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    filterRecipe()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun loadRecipes() {

        firestore.collection("recipes")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener {

                recipeList.clear()

                allRecipes.clear()

                for (doc in it) {

                    val recipe =
                        AdminRecipe(

                            id = doc.id,

                            title =
                                doc.getString("title")
                                    ?: "",

                            image =
                                doc.getString("image")
                                    ?: "",

                            category =
                                doc.getString("category")
                                    ?: "",

                            area =
                                doc.getString("area")
                                    ?: "",

                            instructions =
                                doc.getString("instructions")
                                    ?: "",

                            youtube =
                                doc.getString("youtube")
                                    ?: "",

                            source =
                                doc.getString("source")
                                    ?: "firebase"
                        )

                    recipeList.add(recipe)

                    allRecipes.add(recipe)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Load recipes failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun deleteRecipe(
        recipeId: String
    ) {

        firestore.collection("recipes")
            .document(recipeId)
            .delete()
            .addOnSuccessListener {

                val index =
                    recipeList.indexOfFirst {
                        it.id == recipeId
                    }

                if (index != -1) {

                    recipeList.removeAt(index)

                    adapter.notifyItemRemoved(index)
                }

                allRecipes.removeAll {
                    it.id == recipeId
                }

                Toast.makeText(
                    requireContext(),
                    "Đã xóa món ăn",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Xóa thất bại",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun filterRecipe() {

        val keyword =
            binding.edtSearchRecipe.text
                .toString()
                .trim()
                .lowercase()

        val category =
            binding.spCategory.selectedItem
                .toString()

        recipeList.clear()

        recipeList.addAll(

            allRecipes.filter { recipe ->

                val matchKeyword =

                    recipe.title
                        .lowercase()
                        .contains(keyword)

                val matchCategory =

                    category == "Tất cả"
                            ||
                            recipe.category == category

                matchKeyword && matchCategory
            }
        )

        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        loadRecipes()
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}