package ru.gidevent.androidapp.data.model.booking



data class BookingResponse(
        val id: Long,
        val eventTime: Long,
        val user: Long,
        val advertisement: Long,
        val bookingTime: Long,
        val date: Long,
        val totalPrice: Int,
        val isApproved: Boolean
)
