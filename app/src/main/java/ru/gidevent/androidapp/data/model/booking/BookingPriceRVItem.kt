package ru.gidevent.androidapp.data.model.booking

data class BookingPriceRVItem (
    val priceId: Long,
    val customerCategoryId: Long,
    val name: String,
    val price: Int,
    val count: Int = 0
)