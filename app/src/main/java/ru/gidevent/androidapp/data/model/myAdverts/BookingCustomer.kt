package ru.gidevent.androidapp.data.model.myAdverts

import java.util.Calendar

data class BookingCustomer(
    val priceId: Long,
    val customerCategoryId: Long,
    val name: String,
    val count: Int = 0
)
