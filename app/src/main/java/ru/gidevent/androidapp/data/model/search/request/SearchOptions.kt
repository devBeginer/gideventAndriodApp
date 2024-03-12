package ru.gidevent.androidapp.data.model.request.search



data class SearchOptions(
        var priceFrom: Float?,
        var priceTo: Float?,
        var dateFrom: Long?,
        var dateTo: Long?,
        val categories: List<Long>?,
        var isIndividual: Boolean,
        var isGroup: Boolean,
        var durationFrom: Float?,
        var durationTo: Float?,
        val transport: List<Long>?,
        var ageRestriction: Int?,
        var city: Long?,
        var ratingFrom: Float?,
        var ratingTo: Float?
)
