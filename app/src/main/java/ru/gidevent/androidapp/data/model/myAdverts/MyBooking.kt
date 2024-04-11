package ru.gidevent.androidapp.data.model.myAdverts

import java.util.Calendar

data class MyBooking(
    val id: Long,
    val name: String,
    val count: Int,
    val date: Calendar,
    val cost: Int,
    val isConfirmed: Boolean
)
