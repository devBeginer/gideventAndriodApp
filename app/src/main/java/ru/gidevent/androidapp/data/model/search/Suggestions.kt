package ru.gidevent.RestAPI.model.response

import ru.gidevent.RestAPI.model.dto.AdvertSuggestion
import ru.gidevent.RestAPI.model.dto.CategorySuggestion
import ru.gidevent.RestAPI.model.dto.CitySuggestion


data class Suggestions(
        val advertList: List<AdvertSuggestion>,
        val cityList: List<CitySuggestion>,
        val categoryList: List<CategorySuggestion>,
)
