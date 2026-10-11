package com.example.smart_cook.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_cook.databinding.ActivityRegisterBinding
import com.example.smart_cook.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityRegisterBinding

    private lateinit var auth:
            FirebaseAuth

    private lateinit var firestore:
            FirebaseFirestore

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityRegisterBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        // FIREBASE

        auth =
            FirebaseAuth.getInstance()

        firestore =
            FirebaseFirestore.getInstance()

        // REGISTER BUTTON

        binding.btnRegister.setOnClickListener {

            registerUser()
        }

        // LOGIN TEXT

        binding.txtLogin.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }

        // GOOGLE LOGIN

        binding.btnGoogle.setOnClickListener {

            Toast.makeText(
                this,
                "Google Login Coming Soon 🔥",
                Toast.LENGTH_SHORT
            ).show()
        }

        // FACEBOOK LOGIN

        binding.btnFacebook.setOnClickListener {

            Toast.makeText(
                this,
                "Facebook Login Coming Soon 🔥",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // REGISTER USER
    // =========================

    private fun registerUser() {

        val name =
            binding.edtName.text
                .toString()
                .trim()

        val email =
            binding.edtEmail.text
                .toString()
                .trim()

        val password =
            binding.edtPassword.text
                .toString()
                .trim()

        val confirmPassword =
            binding.edtConfirmPassword.text
                .toString()
                .trim()

        // =========================
        // VALIDATION
        // =========================

        if (
            name.isEmpty() ||
            email.isEmpty() ||
            password.isEmpty() ||
            confirmPassword.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Please fill all fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // EMAIL VALID

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            Toast.makeText(
                this,
                "Invalid email format",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // PASSWORD LENGTH

        if (password.length < 6) {

            Toast.makeText(
                this,
                "Password must be at least 6 characters",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // CONFIRM PASSWORD

        if (password != confirmPassword) {

            Toast.makeText(
                this,
                "Password does not match",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // TERMS

        if (!binding.cbTerms.isChecked) {

            Toast.makeText(
                this,
                "Please accept terms",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =========================
        // LOADING BUTTON
        // =========================

        binding.btnRegister.text =
            "Creating..."

        binding.btnRegister.isEnabled =
            false

        // =========================
        // FIREBASE REGISTER
        // =========================

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                val uid =
                    auth.currentUser?.uid ?: ""

                // USER MODEL

                val user =
                    User(
                        uid = uid,
                        name = name,
                        email = email,
                        role = "user"
                    )

                // SAVE FIRESTORE

                firestore
                    .collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Register Success ❤️",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this,
                                LoginActivity::class.java
                            )
                        )

                        finish()
                    }

                    .addOnFailureListener {

                        binding.btnRegister.text =
                            "Create Account"

                        binding.btnRegister.isEnabled =
                            true

                        Toast.makeText(
                            this,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

            } else {

                binding.btnRegister.text =
                    "Create Account"

                binding.btnRegister.isEnabled =
                    true

                Toast.makeText(
                    this,
                    task.exception?.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}