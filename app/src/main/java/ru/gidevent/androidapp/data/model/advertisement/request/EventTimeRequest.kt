package ru.gidevent.androidapp.data.model.advertisement.request

data class EventTimeRequest(
        val timeId: Long,
        val advertisement: Long,
        val time: Long,
        val isRepeatable: Boolean,
        val daysOfWeek: String,
        val startDate: Long,
        val endDate: Long
)