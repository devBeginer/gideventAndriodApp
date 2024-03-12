package ru.gidevent.androidapp.data.model.search

import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant

data class OptionsVariants(
    val transport: List<TransportationVariant>,
    val category: List<Category>
)
