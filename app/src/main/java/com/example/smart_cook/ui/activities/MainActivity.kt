package com.example.smart_cook.ui.activities

import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController

import com.example.smart_cook.R
import com.example.smart_cook.databinding.ActivityMainBinding
import com.example.smart_cook.utils.CloudinaryManager
import com.example.smart_cook.utils.ThemeManager

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityMainBinding

    private lateinit var navController:
            NavController


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        // =========================
        // THEME
        // =========================

        ThemeManager.applyTheme(
            ThemeManager.isDarkMode(
                this
            )
        )

        super.onCreate(
            savedInstanceState
        )


        binding =
            ActivityMainBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )


        // =========================
        // CLOUDINARY
        // =========================

        CloudinaryManager.init(
            this
        )


        // =========================
        // NAVIGATION
        // =========================

        setupNavigation()
    }


    private fun setupNavigation() {

        val navHostFragment =
            supportFragmentManager
                .findFragmentById(
                    R.id.nav_host_fragment
                ) as NavHostFragment

        navController =
            navHostFragment.navController


        // =========================
        // TOP LEVEL DESTINATIONS
        // =========================

        val appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.homeFragment,
                    R.id.searchFragment,
                    R.id.chatFragment,
                    R.id.favoriteFragment,
                    R.id.profileFragment
                )
            )


        // =========================
        // TOOLBAR
        // =========================

        binding.toolbar
            .setupWithNavController(
                navController,
                appBarConfiguration
            )


        // =========================
        // BOTTOM NAVIGATION
        // =========================

        binding.bottomNavigationView
            .setupWithNavController(
                navController
            )


        // =========================
        // DESTINATION CHANGES
        // =========================

        navController
            .addOnDestinationChangedListener {
                    _,
                    destination,
                    _ ->

                when (destination.id) {

                    // =========================
                    // MAIN TABS
                    // =========================

                    R.id.homeFragment,
                    R.id.searchFragment,
                    R.id.chatFragment,
                    R.id.favoriteFragment,
                    R.id.profileFragment -> {

                        binding
                            .bottomNavigationView
                            .visibility =
                            View.VISIBLE
                    }


                    // =========================
                    // SECONDARY SCREENS
                    // =========================

                    R.id.notificationFragment,
                    R.id.recipeDetailFragment -> {

                        binding
                            .bottomNavigationView
                            .visibility =
                            View.GONE
                    }


                    else -> {

                        binding
                            .bottomNavigationView
                            .visibility =
                            View.GONE
                    }
                }
            }
    }
}