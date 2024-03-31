package ru.gidevent.androidapp.data.model.booking



data class GroupRequest (
        val id: Long=0,
        val customerCategory: Long,
        val count: Int,
        val booking: Long
)