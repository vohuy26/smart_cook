package com.example.smart_cook.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.smart_cook.R
import com.example.smart_cook.databinding.ActivityMainBinding
import com.example.smart_cook.utils.CloudinaryManager
import com.example.smart_cook.utils.ThemeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply Dark Mode
        ThemeManager.applyTheme(
            ThemeManager.isDarkMode(this)
        )

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Init Cloudinary
        CloudinaryManager.init(this)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {

        val navHostFragment =
            supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment

        val navController =
            navHostFragment.navController

        binding.bottomNavigationView
            .setupWithNavController(navController)
    }
}