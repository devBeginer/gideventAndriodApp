package ru.gidevent.androidapp.data.model.advertisement.dto

import java.util.Calendar

data class EventTime(
        val timeId: Long,
        val time: Calendar,
        val isRepeatable: Boolean,
        val daysOfWeek: String,
        val startDate: Calendar,
        val endDate: Calendar
)
