package com.example.smart_cook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_cook.databinding.FragmentNotificationBinding
import com.example.smart_cook.model.Notification
import com.example.smart_cook.ui.adapters.NotificationAdapter
import com.google.firebase.firestore.FirebaseFirestore

class NotificationFragment : Fragment() {

    private var _binding:
            FragmentNotificationBinding? = null

    private val binding get() = _binding!!

    private lateinit var firestore:
            FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentNotificationBinding.inflate(
                inflater,
                container,
                false
            )

        firestore =
            FirebaseFirestore.getInstance()

        loadNotifications()

        return binding.root
    }

    private fun loadNotifications() {

        firestore.collection("notifications")
            .get()
            .addOnSuccessListener {

                val list =
                    it.toObjects(
                        Notification::class.java
                    )

                binding.rvNotifications.layoutManager =
                    LinearLayoutManager(
                        requireContext()
                    )

                binding.rvNotifications.adapter =
                    NotificationAdapter(list)
            }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}