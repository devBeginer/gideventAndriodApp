package ru.gidevent.androidapp.data.model.auth.request



data class SellerRequest(
        val sellerId: Long,
        val login: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val photo: String,
        val about: String
)
