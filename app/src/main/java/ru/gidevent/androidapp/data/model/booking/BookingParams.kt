package ru.gidevent.androidapp.data.model.booking

import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.data.model.advertisement.response.TicketPriceResponse

data class BookingParams(
    val eventTimeList: List<EventTime>,
    val price: List<BookingPriceRVItem>
)
