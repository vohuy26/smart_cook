package com.example.smart_cook.model

data class UserRecipe(

    val id: String = "",

    // Người tạo
    val ownerId: String = "",
    val ownerName: String = "",

    // Schema tương thích recipes hiện tại
    val title: String = "",
    val category: String = "",
    val area: String = "",
    val image: String = "",
    val instructions: String = "",
    val youtube: String = "",

    // Dữ liệu mở rộng cho User Recipe
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val cookingTime: Int = 0,
    val servings: Int = 1,

    // "api" / "firebase"
    val source: String = "firebase",

    // ADMIN / USER
    val creatorType: String = "USER",

    // Duyệt bài
    val status: String = RecipeStatus.PENDING.name,
    val approvedBy: String = "",
    val approvedAt: Long? = null,
    val rejectionReason: String = "",

    // Social
    val favoriteCount: Int = 0,
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0,
    val commentCount: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)