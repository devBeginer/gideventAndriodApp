package ru.gidevent.androidapp.ui.state

sealed class UIStateEditAdvert {
    object Loading: UIStateEditAdvert()
    object ConnectionError: UIStateEditAdvert()
    object Idle: UIStateEditAdvert()
    object Unauthorised: UIStateEditAdvert()
    class Error(val message: String): UIStateEditAdvert()
    class SuccessResponse<T>(val data: T): UIStateEditAdvert()
    class SuccessEdit<T>(val data: T): UIStateEditAdvert()
    class SuccessCreate<T>(val data: T): UIStateEditAdvert()
}