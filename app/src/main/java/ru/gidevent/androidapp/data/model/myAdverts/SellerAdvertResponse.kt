package ru.gidevent.androidapp.data.model.myAdverts

data class SellerAdvertResponse(
        val id: Long,
        val advertisement: String,
        val totalPrice: Int,
        val customerCount: Int,
        val status: String
)
