package ru.gidevent.androidapp.data.model.booking

import ru.gidevent.androidapp.data.model.booking.GroupRequest

data class BookingRequest(
        val id: Long=0,
        val eventTime: Long,
        val advertisement: Long,
        val date: Long,
        val totalPrice: Int,
        val idApproved: Boolean,
        val groups: List<GroupRequest>
)
