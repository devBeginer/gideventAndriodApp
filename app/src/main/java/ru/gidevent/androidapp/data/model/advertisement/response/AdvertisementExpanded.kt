package ru.gidevent.androidapp.data.model.advertisement.response

import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.City
import ru.gidevent.RestAPI.model.TransportationVariant

data class AdvertisementExpanded(
    val id: Long,
    val name: String,
    val duration: Int,
    val description: String,
    val transportation: TransportationVariant,
    val ageRestrictions: Int,
    val visitorsCount: Int,
    val isIndividual: Boolean,
    val photos: String,
    val rating: Float,
    val category: Category,
    val city: City,
    val favourite: Boolean?,
    val seller: Seller,
    val priceList: List<TicketPriceDto>?,
    val reviewsList: List<Feedback>?,
    val eventTimeList: List<EventTimeResponse>?
)
