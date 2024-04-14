package ru.gidevent.androidapp.data.model.auth.response


data class SellerResponse(
        val sellerId: Long,
        val user: RegisterBodyResponse,
        val photo: String,
        val about: String
)

