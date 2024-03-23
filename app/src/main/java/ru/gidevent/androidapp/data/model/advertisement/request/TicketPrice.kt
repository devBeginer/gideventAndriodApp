package ru.gidevent.androidapp.data.model.advertisement.request

import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory

data class TicketPrice(
        val priceId: Long = 0,
        val advertisement: Long,
        val customerCategory: Long,
        val price: Int
)
