package ru.gidevent.androidapp.ui.VKID

data class VkResponse(
    val access_token: String,
    val access_token_id: String,
    val email: String,
    val is_service: Boolean,
    val phone: String,
    val phone_validated: Int,
    val source: Int,
    val source_description: String,
    val user_id: String
)