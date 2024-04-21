package ru.gidevent.androidapp.ui.state

sealed class UIStateEditProfile {
    object Loading: UIStateEditProfile()
    object ConnectionError: UIStateEditProfile()
    object Idle: UIStateEditProfile()
    object Unauthorised: UIStateEditProfile()
    class Error(val message: String): UIStateEditProfile()
    class Success<T>(val data: T): UIStateEditProfile()
    object SuccessUpdate: UIStateEditProfile()
}