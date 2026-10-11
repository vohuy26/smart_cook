package com.example.smart_cook.ui.admin.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smart_cook.databinding.FragmentAdminDashboardBinding
import com.example.smart_cook.ui.admin.AdminActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAdminDashboardBinding.inflate(
            inflater,
            container,
            false
        )

        binding.btnMenu.setOnClickListener {
            (activity as? AdminActivity)?.openDrawer()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStatistics()
        setupChart()
    }

    /**
     * Load Dashboard Statistics
     */
    private fun loadStatistics() {

        // Recipes
        db.collection("recipes")
            .get()
            .addOnSuccessListener { snapshot ->

                if (!isAdded) return@addOnSuccessListener

                _binding?.let {

                    val total = snapshot.size()

                    it.txtFoods.text = total.toString()
                    it.txtFoodsGrowth.text = "+ $total món mới"

                }

            }
            .addOnFailureListener {

                if (!isAdded) return@addOnFailureListener

                _binding?.txtFoods?.text = "0"

            }

        // Users
        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->

                if (!isAdded) return@addOnSuccessListener

                _binding?.let {

                    val total = snapshot.size()

                    it.txtUsers.text = total.toString()
                    it.txtUsersGrowth.text = "+ $total người dùng"

                }

            }
            .addOnFailureListener {

                if (!isAdded) return@addOnFailureListener

                _binding?.txtUsers?.text = "0"

            }

        // Reviews
        db.collection("reviews")
            .get()
            .addOnSuccessListener { snapshot ->

                if (!isAdded) return@addOnSuccessListener

                _binding?.let {

                    val total = snapshot.size()

                    it.txtReviews.text = total.toString()
                    it.txtReviewsGrowth.text = "+ $total đánh giá"

                }

            }
            .addOnFailureListener {

                if (!isAdded) return@addOnFailureListener

                _binding?.txtReviews?.text = "0"

            }
    }

    /**
     * Setup Chart
     */
    private fun setupChart() {

        val entries = mutableListOf<Entry>().apply {

            add(Entry(1f, 420f))
            add(Entry(3f, 510f))
            add(Entry(5f, 540f))
            add(Entry(8f, 660f))
            add(Entry(11f, 520f))
            add(Entry(15f, 760f))
            add(Entry(18f, 430f))
            add(Entry(22f, 560f))
            add(Entry(25f, 760f))
            add(Entry(28f, 760f))
            add(Entry(30f, 860f))

        }

        val dataSet = LineDataSet(entries, "").apply {

            color = Color.parseColor("#6C3EF4")
            fillColor = Color.parseColor("#D7C7FF")

            lineWidth = 4f
            circleRadius = 5f

            setCircleColor(Color.parseColor("#6C3EF4"))
            setDrawCircleHole(false)

            setDrawFilled(true)

            valueTextColor = Color.TRANSPARENT

            mode = LineDataSet.Mode.CUBIC_BEZIER

        }

        binding.lineChart.apply {

            data = LineData(dataSet)

            description.isEnabled = false
            legend.isEnabled = false

            axisRight.isEnabled = false

            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)

            axisLeft.setDrawLabels(false)
            xAxis.setDrawLabels(false)

            axisLeft.axisLineColor = Color.TRANSPARENT
            xAxis.axisLineColor = Color.TRANSPARENT

            setDrawBorders(false)

            setScaleEnabled(false)
            setPinchZoom(false)
            isDragEnabled = false
            setTouchEnabled(false)

            animateXY(1500, 1500)

            invalidate()
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}