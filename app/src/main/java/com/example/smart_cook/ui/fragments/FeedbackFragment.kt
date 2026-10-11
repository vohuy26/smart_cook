package com.example.smart_cook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smart_cook.databinding.FragmentFeedbackBinding
import com.example.smart_cook.model.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackFragment : Fragment() {

    private var _binding:
            FragmentFeedbackBinding? = null

    private val binding get() = _binding!!

    private lateinit var firestore:
            FirebaseFirestore

    private lateinit var auth:
            FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentFeedbackBinding.inflate(
                inflater,
                container,
                false
            )

        firestore =
            FirebaseFirestore.getInstance()

        auth =
            FirebaseAuth.getInstance()

        setupSubmit()

        return binding.root
    }

    private fun setupSubmit() {

        binding.btnSubmit.setOnClickListener {

            submitFeedback()
        }
    }

    private fun submitFeedback() {

        val uid =
            auth.currentUser?.uid ?: return

        val comment =
            binding.edtComment.text.toString()

        val rating =
            binding.ratingBar.rating

        val feedback =
            Feedback(
                uid = uid,
                username =
                    auth.currentUser?.email ?: "",
                rating = rating,
                comment = comment
            )

        firestore.collection("feedbacks")
            .add(feedback)
            .addOnSuccessListener {

                Toast.makeText(
                    requireContext(),
                    "Thank you for feedback ❤️",
                    Toast.LENGTH_SHORT
                ).show()

                binding.edtComment.setText("")
                binding.ratingBar.rating = 0f
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}