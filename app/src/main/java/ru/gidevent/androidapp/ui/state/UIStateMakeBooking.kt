package ru.gidevent.androidapp.ui.state

sealed class UIStateMakeBooking {
    object Loading: UIStateMakeBooking()
    object ConnectionError: UIStateMakeBooking()
    object Idle: UIStateMakeBooking()
    object Unauthorised: UIStateMakeBooking()
    class Error(val message: String): UIStateMakeBooking()
    class SuccessBook<T>(val data: T): UIStateMakeBooking()
    class UpdateUI<T>(val data: T): UIStateMakeBooking()
}