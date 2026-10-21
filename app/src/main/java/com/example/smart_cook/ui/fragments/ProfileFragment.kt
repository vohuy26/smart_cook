package com.example.smart_cook.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import com.bumptech.glide.Glide

import com.example.smart_cook.R
import com.example.smart_cook.databinding.FragmentProfileBinding
import com.example.smart_cook.ui.activities.EditProfileActivity
import com.example.smart_cook.ui.activities.LoginActivity
import com.example.smart_cook.ui.profile.ProfileViewModel
import com.example.smart_cook.utils.ThemeManager
import androidx.navigation.fragment.findNavController

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding:
            FragmentProfileBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            ProfileViewModel by viewModels()


    // =========================
    // IMAGE PICKER
    // =========================

    private val imagePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri ?: return@registerForActivityResult

            // Preview ngay ảnh vừa chọn
            _binding
                ?.imgAvatar
                ?.setImageURI(
                    uri
                )

            // ViewModel xử lý:
            // Cloudinary -> Firestore -> State
            viewModel
                .uploadAvatar(
                    uri.toString()
                )
        }


    // =========================
    // CREATE VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentProfileBinding.inflate(
                inflater,
                container,
                false
            )

        setupDarkMode()

        setupEditProfile()

        setupAvatarPicker()

        setupFeedback()

        setupAddRecipe()

        setupMyRecipes()

        setupLogout()

        return binding.root
    }

    private fun setupFeedback() {

        binding.cardFeedback
            .setOnClickListener {

                findNavController()
                    .navigate(
                        R.id.feedbackFragment
                    )
            }
    }

    private fun setupAddRecipe() {

        binding.cardAddRecipe
            .setOnClickListener {

                findNavController()
                    .navigate(
                        R.id.addUserRecipeFragment
                    )
            }
    }

    private fun setupMyRecipes() {

        binding.cardMyRecipes
            .setOnClickListener {

                findNavController()
                    .navigate(
                        R.id.myRecipesFragment
                    )
            }
    }


    // =========================
    // VIEW CREATED
    // =========================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        observeProfileState()
    }


    // =========================
    // OBSERVE STATE
    // =========================

    private fun observeProfileState() {

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        // =========================
                        // UI STATE
                        // =========================

                        launch {

                            viewModel
                                .uiState
                                .collect { state ->

                                    binding.txtUsername.text =
                                        state.username

                                    binding.txtEmail.text =
                                        state.email

                                    binding.txtFavoriteCount.text =
                                        state.favoriteCount
                                            .toString()


                                    // =========================
                                    // AVATAR
                                    // =========================

                                    /*
                                     * Khi đang upload:
                                     * giữ preview local mà user vừa chọn.
                                     *
                                     * Khi upload xong:
                                     * avatarUrl thay đổi và Glide
                                     * load URL Cloudinary mới.
                                     */
                                    if (!state.isUploadingAvatar) {

                                        showAvatar(
                                            state.avatarUrl
                                        )
                                    }


                                    // Không cho bấm avatar
                                    // liên tục khi đang upload.
                                    binding.imgAvatar.isEnabled =
                                        !state.isUploadingAvatar


                                    state.error
                                        ?.let { message ->

                                            Toast.makeText(
                                                requireContext(),
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                        }


                        // =========================
                        // MESSAGE
                        // =========================

                        launch {

                            viewModel
                                .message
                                .collect { message ->

                                    Toast.makeText(
                                        requireContext(),
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
            }
    }


    // =========================
    // AVATAR UI
    // =========================

    private fun showAvatar(
        avatarUrl: String?
    ) {

        if (!avatarUrl.isNullOrBlank()) {

            Glide.with(
                binding.imgAvatar
            )
                .load(
                    avatarUrl
                )
                .placeholder(
                    R.drawable.ic_chef
                )
                .error(
                    R.drawable.ic_chef
                )
                .circleCrop()
                .into(
                    binding.imgAvatar
                )

        } else {

            binding.imgAvatar
                .setImageResource(
                    R.drawable.ic_chef
                )
        }
    }


    // =========================
    // AVATAR PICKER
    // =========================

    private fun setupAvatarPicker() {

        binding.imgAvatar
            .setOnClickListener {

                imagePickerLauncher
                    .launch(
                        "image/*"
                    )
            }
    }


    // =========================
    // DARK MODE
    // =========================

    private fun setupDarkMode() {

        val isDarkMode =
            ThemeManager
                .isDarkMode(
                    requireContext()
                )

        binding.switchDark.isChecked =
            isDarkMode


        binding.switchDark
            .setOnCheckedChangeListener { _, isChecked ->

                ThemeManager
                    .saveTheme(
                        requireContext(),
                        isChecked
                    )

                ThemeManager
                    .applyTheme(
                        isChecked
                    )
            }
    }


    // =========================
    // EDIT PROFILE
    // =========================

    private fun setupEditProfile() {

        binding.cardEditProfile
            .setOnClickListener {

                startActivity(
                    Intent(
                        requireContext(),
                        EditProfileActivity::class.java
                    )
                )
            }
    }


    // =========================
    // LOGOUT
    // =========================

    private fun setupLogout() {

        binding.btnLogout
            .setOnClickListener {

                // Firebase logout nằm trong
                // UserRepository -> ViewModel
                viewModel.logout()


                // Navigation vẫn thuộc UI layer
                val intent =
                    Intent(
                        requireContext(),
                        LoginActivity::class.java
                    )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(
                    intent
                )
            }
    }


    // =========================
    // RESUME
    // =========================

    override fun onResume() {

        super.onResume()

        /*
         * Refresh khi:
         * - vừa mở Profile
         * - quay lại từ EditProfileActivity
         * - favorite count thay đổi
         */
        viewModel
            .loadProfile()
    }


    // =========================
    // DESTROY
    // =========================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}