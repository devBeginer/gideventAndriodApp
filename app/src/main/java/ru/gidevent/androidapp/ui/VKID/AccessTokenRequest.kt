package ru.gidevent.androidapp.ui.VKID

data class AccessTokenRequest(
    val access_token: String,
    val event: List<String>,
    val token: List<String>,
    val uuid: List<String>,
    val v: String
)