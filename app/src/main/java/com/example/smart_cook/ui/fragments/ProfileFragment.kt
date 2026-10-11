package com.example.smart_cook.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.smart_cook.R
import com.example.smart_cook.data.local.AppDatabase
import com.example.smart_cook.databinding.FragmentProfileBinding
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.ui.activities.EditProfileActivity
import com.example.smart_cook.ui.activities.LoginActivity
import com.example.smart_cook.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var repository: FavoriteRepository

    private var imageUri: Uri? = null

    // Image Picker
    private val imagePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {

                imageUri = it

                // Hiển thị ngay
                binding.imgAvatar.setImageURI(it)

                // Upload Cloudinary
                uploadAvatarToCloudinary(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        initFirebase()

        initDatabase()

        loadUserInfo()

        loadFavoriteCount()

        setupDarkMode()

        setupEditProfile()

        setupAvatarUpload()

        setupLogout()

        return binding.root
    }

    private fun initFirebase() {

        auth = FirebaseAuth.getInstance()

        firestore = FirebaseFirestore.getInstance()
    }

    private fun initDatabase() {

        val dao = AppDatabase
            .getDatabase(requireContext())
            .favoriteDao()

        repository = FavoriteRepository(dao)
    }

    private fun loadUserInfo() {

        val currentUser = auth.currentUser

        if (currentUser == null) {

            binding.txtUsername.text = "Guest"

            binding.txtEmail.text = "No Email"

            return
        }

        // Email
        binding.txtEmail.text =
            currentUser.email ?: "No Email"

        // Firestore user data
        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                val username =
                    document.getString("username")
                        ?: "Smart Cook User"

                binding.txtUsername.text = username

                // Avatar
                val avatarUrl =
                    document.getString("avatar")

                if (!avatarUrl.isNullOrEmpty()) {

                    Glide.with(requireContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_chef)
                        .error(R.drawable.ic_chef)
                        .circleCrop()
                        .into(binding.imgAvatar)

                } else {

                    binding.imgAvatar.setImageResource(
                        R.drawable.ic_chef
                    )
                }
            }
    }

    private fun setupAvatarUpload() {

        binding.imgAvatar.setOnClickListener {

            imagePickerLauncher.launch("image/*")
        }
    }

    // Upload avatar to Cloudinary
    private fun uploadAvatarToCloudinary(uri: Uri) {

        MediaManager.get().upload(uri)
            .unsigned("smartcook_avatar")
            .option("folder", "smartcook_avatar")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {

                }

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {

                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: MutableMap<Any?, Any?>?
                ) {

                    val imageUrl =
                        resultData?.get("secure_url") as? String

                    if (!imageUrl.isNullOrEmpty()) {

                        saveAvatarUrl(imageUrl)
                    }
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                    println(error?.description)
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                }
            })
            .dispatch()
    }

    // Save avatar url to Firestore
    private fun saveAvatarUrl(url: String) {

        val currentUser = auth.currentUser ?: return

        val updates = hashMapOf<String, Any>(
            "avatar" to url
        )

        firestore.collection("users")
            .document(currentUser.uid)
            .set(
                updates,
                com.google.firebase.firestore.SetOptions.merge()
            )
            .addOnSuccessListener {

                println("FIRESTORE SAVE SUCCESS")

                Glide.with(requireContext())
                    .load(url)
                    .circleCrop()
                    .into(binding.imgAvatar)
            }
            .addOnFailureListener {

                println("FIRESTORE ERROR = ${it.message}")
            }
    }

    private fun setupDarkMode() {

        val isDarkMode =
            ThemeManager.isDarkMode(
                requireContext()
            )

        binding.switchDark.isChecked =
            isDarkMode

        binding.switchDark
            .setOnCheckedChangeListener { _, isChecked ->

                ThemeManager.saveTheme(
                    requireContext(),
                    isChecked
                )

                ThemeManager.applyTheme(
                    isChecked
                )
            }
    }

    private fun loadFavoriteCount() {

        lifecycleScope.launch {

            try {

                val favoriteList =
                    repository.getAllFavorites()

                binding.txtFavoriteCount.text =
                    favoriteList.size.toString()

            } catch (e: Exception) {

                binding.txtFavoriteCount.text = "0"
            }
        }
    }

    private fun setupEditProfile() {

        binding.cardEditProfile.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    EditProfileActivity::class.java
                )
            )
        }
    }

    private fun setupLogout() {

        binding.btnLogout.setOnClickListener {

            auth.signOut()

            val intent = Intent(
                requireContext(),
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    override fun onResume() {

        super.onResume()

        loadUserInfo()
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}