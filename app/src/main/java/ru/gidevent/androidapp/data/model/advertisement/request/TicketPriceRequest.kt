package ru.gidevent.androidapp.data.model.advertisement.request

data class TicketPriceRequest(
        val priceId: Long = 0,
        val advertisement: Long,
        val customerCategory: Long,
        val price: Int
)
