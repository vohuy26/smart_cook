package com.example.smart_cook.ui.admin.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_cook.databinding.FragmentAdminReviewBinding
import com.example.smart_cook.model.Review
import com.example.smart_cook.ui.admin.adapter.AdminReviewAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.widget.AdapterView

class AdminReviewFragment : Fragment() {

    private var _binding: FragmentAdminReviewBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()

    private val reviewList = mutableListOf<Review>()
    private val allReviews = mutableListOf<Review>()

    private lateinit var adapter: AdminReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAdminReviewBinding.inflate(
            inflater,
            container,
            false
        )

        setupRecycler()
        setupSearch()
        setupRatingFilter()
        loadReviews()

        return binding.root
    }

    private fun setupRecycler() {

        adapter = AdminReviewAdapter(
            reviewList
        ) { review ->

            showDeleteDialog(review)
        }

        binding.rvReviews.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvReviews.adapter = adapter
    }

    private fun setupSearch() {

        binding.edtSearchReview.addTextChangedListener {

            filterReviews()
        }
    }

    private fun setupRatingFilter() {


            val ratings = listOf(
                "All",
                "5 Star",
                "4 Star",
                "3 Star",
                "2 Star",
                "1 Star"
            )

            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                ratings
            )

            binding.spinnerRating.adapter = spinnerAdapter

            binding.spinnerRating.setOnItemSelectedListener(
                object : android.widget.AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: android.widget.AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        filterReviews()
                    }

                    override fun onNothingSelected(
                        parent: android.widget.AdapterView<*>?
                    ) {
                    }
                }
            )
    }

    private fun filterReviews() {

        val keyword =
            binding.edtSearchReview.text
                .toString()
                .trim()
                .lowercase()

        val selectedRating =
            try {
                binding.spinnerRating.selectedItem.toString()
            } catch (e: Exception) {
                "All"
            }

        val filtered = allReviews.filter { review ->

            val matchKeyword =

                review.userName.lowercase().contains(keyword)
                        ||
                        review.recipeName.lowercase().contains(keyword)
                        ||
                        review.comment.lowercase().contains(keyword)

            val matchRating = when (selectedRating) {

                "5 Star" -> review.rating.toInt() == 5
                "4 Star" -> review.rating.toInt() == 4
                "3 Star" -> review.rating.toInt() == 3
                "2 Star" -> review.rating.toInt() == 2
                "1 Star" -> review.rating.toInt() == 1

                else -> true
            }

            matchKeyword && matchRating
        }

        adapter.updateList(filtered)
    }

    private fun loadReviews() {

        firestore.collection("reviews")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { documents ->

                reviewList.clear()
                allReviews.clear()

                for (doc in documents) {

                    val review =
                        doc.toObject(
                            Review::class.java
                        )

                    review.id = doc.id

                    reviewList.add(review)
                    allReviews.add(review)
                }

                adapter.updateList(reviewList)
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveAdminLog(
        action: String
    ) {

        val log = hashMapOf(

            "title" to action,

            "time" to System.currentTimeMillis()
        )

        firestore.collection("admin_logs")
            .add(log)
    }

    private fun showDeleteDialog(
        review: Review
    ) {

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Review")
            .setMessage(
                "Are you sure you want to delete this review?"
            )
            .setPositiveButton("Delete") { _, _ ->

                deleteReview(review)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteReview(
        review: Review
    ) {

        firestore.collection("reviews")
            .document(review.id)
            .delete()
            .addOnSuccessListener {

                saveAdminLog(
                    "Deleted review by ${review.userName}"
                )

                adapter.removeReview(review)

                allReviews.remove(review)

                Toast.makeText(
                    requireContext(),
                    "Review deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}