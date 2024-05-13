package ru.gidevent.androidapp.data.model.myAdverts

data class AdminAdvertResponse(
        val id: Long,
        val advertisement: String,
        val totalPrice: Int,
        val customerCount: Int,
        val place: String,
        val status: String
)
