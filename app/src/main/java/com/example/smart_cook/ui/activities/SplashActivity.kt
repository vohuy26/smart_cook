package com.example.smart_cook.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_cook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smart_cook.ui.admin.AdminActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(
            R.layout.activity_splash
        )

        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            val sharedPref =
                getSharedPreferences(
                    "smartcook",
                    MODE_PRIVATE
                )

            val isFirstTime =
                sharedPref.getBoolean(
                    "onboarding",
                    true
                )

            if (isFirstTime) {

                startActivity(
                    Intent(
                        this,
                        OnboardingActivity::class.java
                    )
                )

            } else {

                val auth =
                    FirebaseAuth.getInstance()

                if (auth.currentUser != null) {

                    val uid = auth.currentUser!!.uid

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->

                            val role =
                                document.getString("role")
                                    ?: "user"

                            if (role == "admin") {

                                startActivity(
                                    Intent(
                                        this,
                                        AdminActivity::class.java
                                    )
                                )

                            } else {

                                startActivity(
                                    Intent(
                                        this,
                                        MainActivity::class.java
                                    )
                                )
                            }

                            finish()
                        }

                } else {

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )

                    finish()
                }
            }

            finish()

        }, 2500)
    }
}