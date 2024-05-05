package ru.gidevent.androidapp.ui.VKID

data class Succes(
    val expires: Int,
    val first_name: String,
    val last_name: String,
    val phone: String,
    val phone_validated: Boolean,
    val photo_100: String,
    val photo_200: String,
    val photo_50: String,
    val token: String
)