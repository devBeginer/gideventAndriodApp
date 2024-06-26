package ru.gidevent.androidapp.data.model.auth.response

import ru.gidevent.androidapp.data.model.auth.UserRoles

data class UserDetailsResponse(
    val id: Long,
    val photo: String,
    val firstName: String,
    val lastName: String,
    val bookingsCount: Int,
    val todayBookingsCount: Int,
    val advertsCount: Int,
    val ordersCount: Int,
    val roles: Set<UserRoles>
)