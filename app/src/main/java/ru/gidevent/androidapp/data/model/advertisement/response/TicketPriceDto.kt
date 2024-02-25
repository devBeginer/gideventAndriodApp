package ru.gidevent.RestAPI.model.dto


data class TicketPriceDto(
        val priceId: Long = 0,
        val advertisementId: Long,
        val customerCategoryId: Long,
        val name: String,
        val price: Int
)
