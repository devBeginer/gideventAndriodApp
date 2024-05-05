package ru.gidevent.androidapp.data.model.auth.response

data class Seller(
    val id: Long,
    val photo: String,
    val firstName: String,
    val lastName: String,
    val about: String,
    val advertsCount: Int,
    val feedbackCount: Int,
    val averageRating: Float,
    val isVerified: Boolean
)
