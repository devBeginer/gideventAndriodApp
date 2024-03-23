package ru.gidevent.androidapp.data.model.advertisement.response

data class EventTimeResponse(
        val timeId: Long,
        val time: Long,
        val isRepeatable: Boolean,
        val daysOfWeek: String,
        val startDate: Long,
        val endDate: Long
)