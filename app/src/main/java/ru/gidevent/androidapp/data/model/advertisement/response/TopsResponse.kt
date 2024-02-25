package ru.gidevent.RestAPI.response

import ru.gidevent.RestAPI.model.Category
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement


data class TopsResponse(
        val general: List<Advertisement>,
        val categories: List<Category>,
        val top: List<Advertisement>
)
