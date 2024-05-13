package ru.gidevent.androidapp.data.model.myAdverts

import java.util.Calendar

data class MyAdvert(
    val id: Long,
    val name: String,
    val count: Int,
    val cost: Int,
    val place: String,
    val status: String
)
