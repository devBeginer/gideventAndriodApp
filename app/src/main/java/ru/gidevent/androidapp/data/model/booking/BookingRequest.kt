package ru.gidevent.androidapp.data.model.booking

data class BookingRequest(
        val id: Long=0,
        val eventTime: Long,
        val advertisement: Long,
        val date: Long,
        val totalPrice: Int,
        val isApproved: Boolean,
        val groups: List<GroupRequest>
)
