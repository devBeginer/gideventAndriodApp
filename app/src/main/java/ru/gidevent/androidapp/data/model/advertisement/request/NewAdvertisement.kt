package ru.gidevent.androidapp.data.model.advertisement.request

data class NewAdvertisement(
    val id: Long,
    val name: String,
    val duration: Int,
    val description: String,
    val transportation: /*TransportationVariant*/Long,
    val ageRestrictions: Int,
    val visitorsCount: Int,
    val isIndividual: Boolean,
    val photos: String,
    val rating: Int,
    val category: /*Category*/Long,
    val city: /*City*/Long/*,
    val priceList: List<TicketPrice>?,
    val schedule: List<EventTimeRequest>?*/
)
