package ru.gidevent.androidapp.data.model.myAdverts

data class BookingInfoResponse(
    val id: Long,
    val advertisement: Long,
    val advertisementName: String,
    val eventTime: Long,
    val date: Long,
    val bookingTime: Long,
    val isApproved: Boolean,
    val user: Long,
    val userName: String,
    val place: String,
    val totalPrice: Int,
    val visitorGroups: List<VisitorsGroupResponse>
)
