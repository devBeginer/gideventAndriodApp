package ru.gidevent.androidapp.data.model.myAdverts

data class SellerBookingResponse(
        val id: Long,
        val eventTime: Long,
        val advertisement: String,
        val date: Long,
        val totalPrice: Int,
        val isApproved: Boolean,
        val customerCount: Int
)
