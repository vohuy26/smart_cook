package com.example.smart_cook.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.smart_cook.R
import com.example.smart_cook.databinding.ActivityOnboardingBinding
import com.example.smart_cook.model.OnboardingItem
import com.example.smart_cook.ui.adapters.OnboardingAdapter
import kotlin.math.abs

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // =========================
        // FULL SCREEN
        // =========================

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        window.decorView.systemUiVisibility =
            (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )

        supportActionBar?.hide()

        // =========================
        // BINDING
        // =========================

        binding =
            ActivityOnboardingBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        // =========================
        // DATA
        // =========================

        val onboardingList = listOf(

            OnboardingItem(
                image = R.drawable.nen1,
                title = "Smart Recipes",
                description =
                    "Khám phá hàng ngàn món ăn thông minh"
            ),

            OnboardingItem(
                image = R.drawable.nen2,
                title = "AI Recommendation",
                description =
                    "AI gợi ý món ăn theo sở thích"
            ),

            OnboardingItem(
                image = R.drawable.nen3,
                title = "Meal Planner",
                description =
                    "Lập kế hoạch ăn uống mỗi ngày"
            )
        )

        // =========================
        // ADAPTER
        // =========================

        val onboardingAdapter =
            OnboardingAdapter(
                onboardingList
            )

        binding.viewPager.adapter =
            onboardingAdapter

        // =========================
        // DOT INDICATOR
        // =========================

        binding.dotsIndicator.attachTo(
            binding.viewPager
        )

        // =========================
        // VIEWPAGER ANIMATION
        // =========================

        binding.viewPager.setPageTransformer {

                page,
                position ->

            page.alpha =
                0.5f + (1 - abs(position)) * 0.5f

            page.translationX = 0f

            page.scaleX = 1f

            page.scaleY = 1f
        }

        // =========================
        // NEXT BUTTON
        // =========================

        binding.btnNext.setOnClickListener {

            if (
                binding.viewPager.currentItem <
                onboardingList.size - 1
            ) {

                binding.viewPager.currentItem += 1

            } else {

                finishOnboarding()
            }
        }

        // =========================
        // SKIP BUTTON
        // =========================

        binding.txtSkip.setOnClickListener {

            finishOnboarding()
        }

        // =========================
        // PAGE CHANGE
        // =========================

        binding.viewPager.registerOnPageChangeCallback(

            object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(
                    position: Int
                ) {

                    super.onPageSelected(position)

                    if (
                        position ==
                        onboardingList.size - 1
                    ) {

                        binding.btnNext.text =
                            "Start"

                    } else {

                        binding.btnNext.text =
                            "Next"
                    }
                }
            }
        )
    }

    // =========================
    // FINISH ONBOARDING
    // =========================

    private fun finishOnboarding() {

        val sharedPref =
            getSharedPreferences(
                "smartcook",
                MODE_PRIVATE
            )

        sharedPref.edit()
            .putBoolean(
                "onboarding",
                false
            )
            .apply()

        startActivity(

            Intent(
                this,
                LoginActivity::class.java
            )
        )

        finish()
    }
}