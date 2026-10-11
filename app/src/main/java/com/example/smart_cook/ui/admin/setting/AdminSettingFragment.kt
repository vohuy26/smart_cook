package com.example.smart_cook.ui.admin.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smart_cook.databinding.FragmentAdminSettingBinding
import com.example.smart_cook.ui.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class AdminSettingFragment : Fragment() {

    private var _binding: FragmentAdminSettingBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAdminSettingBinding.inflate(inflater, container, false)

        val user = auth.currentUser

        binding.txtEmail.text =
            user?.email ?: "Unknown"

        binding.btnAbout.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Smart Cook\nVersion 1.0",
                Toast.LENGTH_SHORT
            ).show()

        }

        binding.btnChangePassword.setOnClickListener {

            user?.email?.let { email ->

                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {

                        Toast.makeText(
                            requireContext(),
                            "Đã gửi email đổi mật khẩu.",
                            Toast.LENGTH_LONG
                        ).show()

                    }

            }

        }

        binding.btnLogout.setOnClickListener {

            logout()

        }

        return binding.root
    }

    private fun logout() {

        AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất?")
            .setPositiveButton("Có") { _, _ ->

                auth.signOut()

                val intent =
                    Intent(requireContext(), LoginActivity::class.java)

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

            }

            .setNegativeButton("Không", null)

            .show()

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}