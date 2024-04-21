package ru.gidevent.androidapp.data.model.auth.response

import ru.gidevent.androidapp.data.model.auth.UserRoles


data class ProfileResponse(
    val id: Long = 0,
    val login: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<UserRoles>
)