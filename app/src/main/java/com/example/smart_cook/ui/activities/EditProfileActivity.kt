package com.example.smart_cook.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_cook.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadUserData()

        setupSaveButton()
    }

    private fun loadUserData() {

        val user = auth.currentUser ?: return

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                binding.edtUsername.setText(
                    document.getString("username")
                )

                binding.edtEmail.setText(
                    user.email
                )
            }
    }

    private fun setupSaveButton() {

        binding.btnSave.setOnClickListener {

            val username =
                binding.edtUsername.text.toString().trim()

            if (username.isEmpty()) {

                binding.edtUsername.error =
                    "Enter username"

                return@setOnClickListener
            }

            updateProfile(username)
        }
    }

    private fun updateProfile(username: String) {

        val user = auth.currentUser ?: return

        val updates = hashMapOf<String, Any>(
            "username" to username
        )

        firestore.collection("users")
            .document(user.uid)
            .update(updates)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Profile updated",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Update failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}