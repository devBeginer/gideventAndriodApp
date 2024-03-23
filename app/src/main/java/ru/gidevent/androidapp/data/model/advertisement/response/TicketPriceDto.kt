package ru.gidevent.androidapp.data.model.advertisement.response

data class TicketPriceDto(
    val priceId: Long,
    val customerCategoryId: Long,
    val name: String,
    val price: Int
)
