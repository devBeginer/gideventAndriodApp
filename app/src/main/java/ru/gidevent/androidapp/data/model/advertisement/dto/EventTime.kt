package ru.gidevent.androidapp.data.model.advertisement.dto

import java.util.Calendar

data class EventTime(
        var timeId: Long,
        var time: Calendar,
        var isRepeatable: Boolean,
        var daysOfWeek: String,
        var startDate: Calendar,
        var endDate: Calendar
)
