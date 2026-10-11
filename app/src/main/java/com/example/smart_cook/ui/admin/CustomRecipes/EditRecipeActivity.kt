package com.example.smart_cook.ui.admin.CustomRecipes

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.smart_cook.R
import com.example.smart_cook.databinding.ActivityEditRecipeBinding
import com.example.smart_cook.utils.CloudinaryManager
import com.google.firebase.firestore.FirebaseFirestore

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditRecipeBinding

    private lateinit var firestore: FirebaseFirestore

    private var recipeId = ""

    private var imageUrl = ""

    private var imageUri: Uri? = null

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {

                imageUri = it

                binding.imgRecipe.setImageURI(it)

                uploadNewImage()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityEditRecipeBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        firestore =
            FirebaseFirestore.getInstance()

        CloudinaryManager.init(this)

        recipeId =
            intent.getStringExtra("recipeId")
                ?: ""

        setupCategorySpinner()

        loadRecipe()

        binding.btnChangeImage.setOnClickListener {

            imagePicker.launch("image/*")
        }

        binding.btnUpdate.setOnClickListener {

            updateRecipe()
        }
    }

    private fun setupCategorySpinner() {

        val adapter =
            ArrayAdapter.createFromResource(

                this,

                R.array.recipe_categories_admin,

                android.R.layout.simple_spinner_item
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spCategory.adapter =
            adapter
    }

    private fun loadRecipe() {

        firestore.collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener { document ->

                binding.edtTitle.setText(
                    document.getString("title")
                )

                binding.edtArea.setText(
                    document.getString("area")
                )

                binding.edtYoutube.setText(
                    document.getString("youtube")
                )

                binding.edtInstructions.setText(
                    document.getString("instructions")
                )

                imageUrl =
                    document.getString("image")
                        ?: ""

                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(
                        android.R.drawable.ic_menu_gallery
                    )
                    .into(binding.imgRecipe)

                setCategorySelection(
                    document.getString("category")
                        ?: ""
                )
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Không tải được dữ liệu",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setCategorySelection(
        category: String
    ) {

        val categories =
            resources.getStringArray(
                R.array.recipe_categories_admin
            )

        val position =
            categories.indexOf(category)

        if (position >= 0) {

            binding.spCategory.setSelection(
                position
            )
        }
    }

    private fun uploadNewImage() {

        val uri = imageUri ?: return

        Toast.makeText(
            this,
            "Đang tải ảnh...",
            Toast.LENGTH_SHORT
        ).show()

        MediaManager.get()
            .upload(uri)
            .unsigned("smartcook_avatar")
            .callback(object : UploadCallback {

                override fun onStart(
                    requestId: String?
                ) {
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

                    imageUrl =
                        resultData?.get("secure_url")
                            .toString()

                    Toast.makeText(
                        this@EditRecipeActivity,
                        "Cập nhật ảnh thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                    Toast.makeText(
                        this@EditRecipeActivity,
                        "Upload thất bại: ${error?.description}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                }
            })
            .dispatch()
    }

    private fun updateRecipe() {

        val title =
            binding.edtTitle.text.toString().trim()

        val area =
            binding.edtArea.text.toString().trim()

        val youtube =
            binding.edtYoutube.text.toString().trim()

        val instructions =
            binding.edtInstructions.text.toString().trim()

        val category =
            binding.spCategory.selectedItem.toString()

        if (title.isEmpty()) {

            binding.edtTitle.error =
                "Nhập tên món ăn"

            return
        }

        if (instructions.isEmpty()) {

            binding.edtInstructions.error =
                "Nhập hướng dẫn chế biến"

            return
        }

        val recipe =
            hashMapOf<String, Any>(

                "title" to title,

                "image" to imageUrl,

                "category" to category,

                "area" to area,

                "youtube" to youtube,

                "instructions" to instructions,

                "source" to "firebase"
            )

        firestore.collection("recipes")
            .document(recipeId)
            .update(recipe as Map<String, Any>)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Cập nhật món ăn thành công",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Cập nhật thất bại",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}