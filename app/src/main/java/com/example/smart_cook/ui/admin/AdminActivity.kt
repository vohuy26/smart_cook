package com.example.smart_cook.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.smart_cook.R
import com.example.smart_cook.databinding.ActivityAdminBinding
import com.example.smart_cook.ui.admin.CustomRecipes.AdminRecipeFragment
import com.example.smart_cook.ui.admin.dashboard.AdminDashboardFragment
import com.example.smart_cook.ui.admin.review.AdminReviewFragment
import com.example.smart_cook.ui.admin.setting.AdminSettingFragment
import com.example.smart_cook.ui.admin.statistic.AdminStatisticFragment

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupDrawer()

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.admin_dashboard
        }
    }

    /**
     * Bottom Navigation
     */
    private fun setupBottomNavigation() {

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.admin_dashboard -> {

                    loadFragment(AdminDashboardFragment())
                    true
                }

                R.id.admin_recipes -> {

                    loadFragment(AdminRecipeFragment())
                    true
                }

                R.id.admin_users -> {

                    loadFragment(AdminUserFragment())
                    true
                }

                R.id.admin_reviews -> {

                    loadFragment(AdminReviewFragment())
                    true
                }

                R.id.admin_stats -> {

                    loadFragment(AdminStatisticFragment())
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Drawer Menu
     */
    private fun setupDrawer() {

        binding.navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_dashboard -> {

                    binding.bottomNavigation.selectedItemId =
                        R.id.admin_dashboard
                }

                R.id.nav_recipe -> {

                    binding.bottomNavigation.selectedItemId =
                        R.id.admin_recipes
                }

                R.id.nav_user -> {

                    binding.bottomNavigation.selectedItemId =
                        R.id.admin_users
                }

                R.id.nav_review -> {

                    binding.bottomNavigation.selectedItemId =
                        R.id.admin_reviews
                }

                R.id.nav_statistic -> {

                    binding.bottomNavigation.selectedItemId =
                        R.id.admin_stats
                }

                // Settings không có ở BottomNavigation
                R.id.nav_setting -> {

                    loadFragment(AdminSettingFragment())
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)

            true
        }
    }

    /**
     * Mở Drawer
     */
    fun openDrawer() {

        binding.drawerLayout.openDrawer(GravityCompat.START)

    }

    /**
     * Đóng Drawer
     */
    fun closeDrawer() {

        binding.drawerLayout.closeDrawer(GravityCompat.START)

    }

    /**
     * Load Fragment
     */
    private fun loadFragment(fragment: Fragment) {

        val current =
            supportFragmentManager.findFragmentById(R.id.adminContainer)

        // Nếu đang ở Fragment này thì không load lại
        if (current != null &&
            current::class == fragment::class) {
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.adminContainer, fragment)
            .commit()
    }

    /**
     * Back Press
     */
    @Suppress("DEPRECATION")
    override fun onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {

            binding.drawerLayout.closeDrawer(GravityCompat.START)

        } else {

            super.onBackPressed()

        }
    }
}