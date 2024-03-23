package ru.gidevent.androidapp.data.model.advertisement.response

import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory

data class TicketPriceResponse(
    val priceId: Long = 0,
    val advertisement: Advertisement,
    val customerCategory: CustomerCategory,
    val price: Int
)
