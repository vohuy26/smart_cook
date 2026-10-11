package com.example.smart_cook.ui.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smart_cook.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import com.bumptech.glide.Glide

import com.example.smart_cook.data.local.AppDatabase
import com.example.smart_cook.data.remote.RetrofitClient

import com.example.smart_cook.databinding.FragmentRecipeDetailBinding

import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.model.Meal

import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository

import kotlinx.coroutines.launch

class RecipeDetailFragment : Fragment() {

    private var _binding:
            FragmentRecipeDetailBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var favoriteRepository:
            FavoriteRepository

    private lateinit var mealRepository:
            MealRepository

    private var youtubeLink = ""

    private var isFavorite = false

    override fun onCreateView(

        inflater: LayoutInflater,

        container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View {



        _binding =
            FragmentRecipeDetailBinding.inflate(
                inflater,
                container,
                false
            )



        val database =
            AppDatabase.getDatabase(
                requireContext()
            )



        favoriteRepository =
            FavoriteRepository(
                database.favoriteDao()
            )



        mealRepository =
            MealRepository()



        val mealId =

            arguments?.getString(
                "mealId"
            ) ?: ""



        if(mealId.isNotEmpty()){


            loadMealDetail(
                mealId
            )

        }

        binding.btnBack.setOnClickListener {


            requireActivity()
                .onBackPressedDispatcher
                .onBackPressed()

        }



        binding.btnYoutube.setOnClickListener {


            if(youtubeLink.isNotEmpty()){


                startActivity(

                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(youtubeLink)
                    )

                )

            }else{


                Toast.makeText(

                    requireContext(),

                    "No Youtube Video",

                    Toast.LENGTH_SHORT

                ).show()

            }

        }



        return binding.root

    }

    // =========================
    // LOAD DETAIL
    // =========================


    private fun loadMealDetail(

        id:String

    ){



        lifecycleScope.launch {


            try {


                /*
                Firebase id thường:
                vn_001

                API id:
                52772
                */


                if(id.startsWith("vn_")){


                    mealRepository
                        .getMealById(id){ meal ->


                            if(meal != null){


                                showMeal(
                                    meal
                                )


                            }


                        }



                }else{



                    val response =

                        RetrofitClient.api
                            .getMealDetail(id)



                    if(response.isSuccessful){



                        val apiMeal =

                            response.body()
                                ?.meals
                                ?.firstOrNull()



                        if(apiMeal != null){



                            val meal =

                                Meal(


                                    id =
                                        apiMeal.id,


                                    title =
                                        apiMeal.title
                                            ?: "",


                                    image =
                                        apiMeal.image,


                                    category =
                                        apiMeal.category
                                            ?: "",


                                    area =
                                        apiMeal.area
                                            ?: "",


                                    instructions =
                                        apiMeal.instructions
                                            ?: "",


                                    source =
                                        "api"

                                )


                            showMeal(
                                meal
                            )


                        }

                    }

                }



            }catch(e:Exception){


                Toast.makeText(

                    requireContext(),

                    e.message,

                    Toast.LENGTH_SHORT

                ).show()

            }


        }


    }

    // =========================
    // SHOW
    // =========================


    private fun showMeal(

        meal:Meal

    ){



        Glide.with(requireContext())

            .load(meal.image)

            .centerCrop()

            .into(
                binding.imgRecipe
            )



        binding.txtTitle.text =
            meal.title



        binding.txtCategory.text =
            meal.category



        binding.txtArea.text =
            meal.area



        binding.txtInstructions.text =
            meal.instructions

        youtubeLink =
            meal.youtube

        binding.txtIngredients.text =
            "Nguyên liệu đang cập nhật"



        lifecycleScope.launch {

            isFavorite =
                favoriteRepository
                    .isFavorite(
                        meal.id
                    )

            updateFavoriteIcon()

        }

        binding.btnFavorite.setOnClickListener {

            toggleFavorite(
                meal
            )

        }


    }

    private fun updateFavoriteIcon() {

        if (isFavorite) {

            binding.btnFavorite.setImageResource(
                android.R.drawable.btn_star_big_on
            )

        } else {

            binding.btnFavorite.setImageResource(
                R.drawable.ic_favorite
            )

        }

    }

    private fun toggleFavorite(

        meal: Meal

    ) {

        lifecycleScope.launch {

            try {

                if (!isFavorite) {

                    val favorite =

                        FavoriteRecipe(

                            id =
                                meal.id,

                            title =
                                meal.title,

                            image =
                                meal.image

                        )

                    favoriteRepository
                        .insertFavorite(
                            favorite
                        )

                    isFavorite = true

                    updateFavoriteIcon()

                    Toast.makeText(

                        requireContext(),

                        "Added Favorite ❤️",

                        Toast.LENGTH_SHORT

                    ).show()

                } else {

                    favoriteRepository
                        .deleteFavorite(

                            FavoriteRecipe(

                                id =
                                    meal.id,

                                title =
                                    meal.title,

                                image =
                                    meal.image

                            )

                        )

                    isFavorite = false

                    updateFavoriteIcon()

                    Toast.makeText(

                        requireContext(),

                        "Removed Favorite",

                        Toast.LENGTH_SHORT

                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(

                    requireContext(),

                    e.message,

                    Toast.LENGTH_SHORT

                ).show()

            }

        }

    }

    override fun onDestroyView(){

        super.onDestroyView()

        _binding = null

    }

}