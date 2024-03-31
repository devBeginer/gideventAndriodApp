package ru.gidevent.androidapp.data.model.booking

import ru.gidevent.androidapp.data.model.advertisement.response.EventTimeResponse
import ru.gidevent.androidapp.data.model.advertisement.response.TicketPriceResponse

data class BookingParamsResponse(
    val eventTimeList: List<EventTimeWithCountResponse>,
    val price: List<TicketPriceResponse>,
    val maxCount: Int
)
