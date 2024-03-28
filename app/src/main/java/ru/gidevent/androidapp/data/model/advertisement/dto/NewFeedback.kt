package ru.gidevent.androidapp.data.model.advertisement.dto

data class NewFeedback(
    val advertisementId: Long,
    val rating: Int,
    val text: String
)
