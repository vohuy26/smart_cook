package com.example.smart_cook.ui.admin.model

data class AdminRecipe(

    val id: String = "",

    val title: String = "",

    val image: String = "",

    val category: String = "",

    val area: String = "",

    val instructions: String = "",

    val youtube: String = "",

    val source: String = "firebase",

    // ADMIN / USER
    val creatorType: String = "ADMIN",

    // Người gửi
    val ownerId: String = "",

    val ownerName: String = "",

    // PENDING / APPROVED / REJECTED
    val status: String = "APPROVED",

    val rejectionReason: String = ""
)