package ru.gidevent.androidapp.data.model.auth.response

import ru.gidevent.androidapp.data.model.auth.UserRoles

data class UserDetailsResponse(
    val id: Long,
    val login: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<UserRoles>
)
