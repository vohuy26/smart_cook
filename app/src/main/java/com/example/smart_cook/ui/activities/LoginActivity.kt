package com.example.smart_cook.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_cook.databinding.ActivityLoginBinding
import com.example.smart_cook.ui.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityLoginBinding

    private lateinit var auth:
            FirebaseAuth

    private lateinit var firestore:
            FirebaseFirestore

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityLoginBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        // =========================
        // FIREBASE
        // =========================

        auth =
            FirebaseAuth.getInstance()

        firestore =
            FirebaseFirestore.getInstance()

        // =========================
        // ANIMATION
        // =========================

        setupAnimation()

        // =========================
        // AUTO LOGIN
        // =========================

        if (auth.currentUser != null) {

            checkUserRole()
        }

        // =========================
        // LOGIN BUTTON
        // =========================

        binding.btnLogin.setOnClickListener {

            loginUser()
        }

        // =========================
        // REGISTER BUTTON
        // =========================

        binding.txtRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }

        // =========================
        // GOOGLE LOGIN
        // =========================

        binding.btnGoogle.setOnClickListener {

            Toast.makeText(
                this,
                "Google Login Coming Soon 🚀",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =========================
        // FACEBOOK LOGIN
        // =========================

        binding.btnFacebook.setOnClickListener {

            Toast.makeText(
                this,
                "Facebook Login Coming Soon 🚀",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // LOGIN USER
    // =========================

    private fun loginUser() {

        val email =
            binding.edtEmail.text
                .toString()
                .trim()

        val password =
            binding.edtPassword.text
                .toString()
                .trim()

        // =========================
        // VALIDATION
        // =========================

        if (email.isEmpty()) {

            binding.edtEmail.error =
                "Please enter email"

            binding.edtEmail.requestFocus()

            return
        }

        if (password.isEmpty()) {

            binding.edtPassword.error =
                "Please enter password"

            binding.edtPassword.requestFocus()

            return
        }

        if (password.length < 6) {

            binding.edtPassword.error =
                "Password must be at least 6 characters"

            binding.edtPassword.requestFocus()

            return
        }

        // =========================
        // LOADING
        // =========================

        binding.btnLogin.isEnabled =
            false

        binding.btnLogin.text =
            "Loading..."

        // =========================
        // FIREBASE LOGIN
        // =========================

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            binding.btnLogin.isEnabled =
                true

            binding.btnLogin.text =
                "Login"

            if (task.isSuccessful) {

                checkUserRole()

            } else {

                Toast.makeText(
                    this,
                    task.exception?.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // =========================
    // CHECK USER ROLE
    // =========================

    private fun checkUserRole() {

        val uid =
            auth.currentUser?.uid ?: ""

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val role =
                        document.getString("role")
                            ?: "user"

                    // =========================
                    // ADMIN
                    // =========================

                    if (role == "admin") {

                        Toast.makeText(
                            this,
                            "Welcome Admin 👑",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this,
                                AdminActivity::class.java
                            )
                        )

                        finish()
                    }

                    // =========================
                    // USER
                    // =========================

                    else {

                        Toast.makeText(
                            this,
                            "Login Success ❤️",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                        )

                        finish()
                    }

                } else {

                    Toast.makeText(
                        this,
                        "User data not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================
    // UI ANIMATION
    // =========================

    private fun setupAnimation() {

        binding.root.alpha = 0f

        binding.root.translationY = 80f

        binding.root.animate()

            .alpha(1f)

            .translationY(0f)

            .setDuration(900)

            .setInterpolator(
                OvershootInterpolator()
            )

            .start()
    }
}