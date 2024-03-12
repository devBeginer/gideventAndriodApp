package ru.gidevent.androidapp.data.model.search

import ru.gidevent.RestAPI.model.City
import java.util.Calendar

data class SearchOptionsDto(
    var priceFrom: Float,
    var priceTo: Float,
    var dateFrom: Calendar?,
    var dateTo: Calendar?,
    val categories: MutableList<Long>,
    var isIndividual: Boolean,
    var isGroup: Boolean,
    var durationFrom: Float,
    var durationTo: Float,
    val transport: MutableList<Long>,
    var ageRestriction: Int?,
    var city: City?,
    var ratingFrom: Float,
    var ratingTo: Float
)
