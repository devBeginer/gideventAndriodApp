package ru.gidevent.androidapp.ui.VKID

data class Payload(
    val auth: Int,
    val hash: String,
    val loadExternalUsers: Boolean,
    val oauth: Oauth,
    val ttl: Int,
    val type: String,
    val user: User,
    val uuid: String
)