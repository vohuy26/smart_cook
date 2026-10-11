package com.example.smart_cook.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_cook.databinding.FragmentAdminUserBinding
import com.example.smart_cook.model.User
import com.example.smart_cook.ui.admin.adapter.AdminUserAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AdminUserFragment : Fragment() {

    private var _binding: FragmentAdminUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdminUserAdapter

    private val userList =
        mutableListOf<User>()

    private val allUsers =
        mutableListOf<User>()

    private val firestore =
        FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAdminUserBinding.inflate(
                inflater,
                container,
                false
            )

        setupRecycler()

        setupSearch()

        loadUsers()

        return binding.root
    }

    private fun setupRecycler() {

        adapter = AdminUserAdapter(

            userList

        ) { user ->

            makeAdmin(user.uid)
        }

        binding.rvUsers.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvUsers.adapter =
            adapter
    }

    private fun setupSearch() {

        binding.edtSearchUser
            .addTextChangedListener {

                val keyword =
                    it.toString()
                        .trim()
                        .lowercase()

                userList.clear()

                if (keyword.isEmpty()) {

                    userList.addAll(
                        allUsers
                    )

                } else {

                    userList.addAll(

                        allUsers.filter { user ->

                            user.name
                                .lowercase()
                                .contains(keyword)

                                    ||

                                    user.email
                                        .lowercase()
                                        .contains(keyword)
                        }
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun loadUsers() {

        firestore.collection("users")
            .get()
            .addOnSuccessListener {

                userList.clear()
                allUsers.clear()

                for (doc in it) {

                    val user =
                        doc.toObject(
                            User::class.java
                        )

                    userList.add(user)

                    allUsers.add(user)
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun deleteUser(
        uid: String
    ) {

        firestore.collection("users")
            .document(uid)
            .delete()
            .addOnSuccessListener {

                loadUsers()
            }
    }

    private fun makeAdmin(
        uid: String
    ) {

        firestore.collection("users")
            .document(uid)
            .update(
                "role",
                "admin"
            )
            .addOnSuccessListener {

                loadUsers()
            }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}