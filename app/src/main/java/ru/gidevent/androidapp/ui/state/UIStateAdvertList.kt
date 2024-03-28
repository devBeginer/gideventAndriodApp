package ru.gidevent.androidapp.ui.state

sealed class UIStateAdvertList {
    object Loading: UIStateAdvertList()
    object ConnectionError: UIStateAdvertList()
    object Idle: UIStateAdvertList()
    object Unauthorised: UIStateAdvertList()
    class Error(val message: String): UIStateAdvertList()
    class Success<T>(val data: T): UIStateAdvertList()
    class Update<T>(val data: T): UIStateAdvertList()
}