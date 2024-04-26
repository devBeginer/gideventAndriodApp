package ru.gidevent.androidapp.ui.VKID

data class Payload(
    val auth: Int,
    val hash: String,
    val oauth: Oauth,
    val user: User,
    val token: String,
    val ttl: Int,
    val type: String,
    val uuid: String,
    val loadExternalUsers: Boolean
)