package ru.gidevent.androidapp.data.model.myAdverts

import java.util.Calendar

data class BookingInfo(
    val id: Long,
    val name: String,
    val count: Int,
    val date: Calendar,
    val cost: Int,
    val confirmed: Boolean,
    val customers: List<BookingCustomer>
)
