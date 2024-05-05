package ru.gidevent.androidapp.data.model.auth.response

import ru.gidevent.androidapp.data.model.auth.UserRoles

data class EditProfile(
    val id: Long,
    val photo: String,
    val firstName: String,
    val lastName: String,
    val about: String,
    val login: String,
    val password: String,
    val roles: Set<UserRoles>,
    val vkUser: Boolean = false
)