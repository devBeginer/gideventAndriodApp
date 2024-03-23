package ru.gidevent.androidapp.data.model.advertisement.response

import com.google.gson.annotations.SerializedName
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.City
import ru.gidevent.RestAPI.model.Seller
import ru.gidevent.RestAPI.model.TransportationVariant

data class Advertisement(
    val id: Long,
    val name: String,
    val duration: Int,
    val description: String,
    val transportation: TransportationVariant,
    val ageRestrictions: Int,
    val visitorsCount: Int,
    val isIndividual: Boolean,
    val photos: String,
    val rating: Int,
    val category: Category,
    val city: City,
    val favourite: Boolean?,
    val seller: Seller,
    @SerializedName("price")
    val priceList: List<TicketPriceDto>?
)
