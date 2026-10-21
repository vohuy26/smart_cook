package com.example.smart_cook.ui.admin.CustomRecipes

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.smart_cook.databinding.ActivityAddRecipeBinding
import com.example.smart_cook.utils.CloudinaryManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.smart_cook.model.RecipeStatus

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecipeBinding
    private lateinit var firestore: FirebaseFirestore

    private var imageUri: Uri? = null
    private var uploadedImageUrl = ""

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {
                imageUri = it
                binding.imgPreview.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityAddRecipeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firestore =
            FirebaseFirestore.getInstance()

        CloudinaryManager.init(this)

        binding.btnChooseImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            uploadImageAndSaveRecipe()
        }
    }

    private fun uploadImageAndSaveRecipe() {

        if (imageUri == null) {

            Toast.makeText(
                this,
                "Vui lòng chọn ảnh",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        binding.progressUpload.visibility =
            View.VISIBLE

        MediaManager.get()
            .upload(imageUri)
            .unsigned("smartcook_avatar")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {}

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

                    binding.progressUpload.visibility =
                        View.GONE

                    uploadedImageUrl =
                        resultData?.get("secure_url")
                            .toString()

                    saveRecipe()
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                    binding.progressUpload.visibility =
                        View.GONE

                    Toast.makeText(
                        this@AddRecipeActivity,
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

    private fun saveRecipe() {

        val title =
            binding.edtTitle.text.toString().trim()

        val category =
            binding.spCategory.selectedItem.toString()

        val area =
            binding.edtArea.text.toString().trim()

        val instructions =
            binding.edtInstructions.text.toString().trim()

        val youtube =
            binding.edtYoutube.text.toString().trim()

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

        val recipe = hashMapOf(

            "title" to title,

            "category" to category,

            "area" to area,

            "image" to uploadedImageUrl,

            "instructions" to instructions,

            "youtube" to youtube,

            "source" to "firebase",

            "favoriteCount" to 0,

            "createdAt" to System.currentTimeMillis(),

            "creatorType" to "ADMIN",

            "status" to
                    RecipeStatus.APPROVED.name,
        )

        firestore.collection("recipes")
            .add(recipe)
            .addOnSuccessListener {

                saveAdminLog(
                    "Added recipe $title"
                )

                Toast.makeText(
                    this,
                    "Thêm món ăn thành công",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun saveAdminLog(action: String) {

        val log = hashMapOf(

            "title" to action,

            "actionType" to "recipe",

            "time" to System.currentTimeMillis()
        )

        firestore.collection("admin_logs")
            .add(log)
    }
}