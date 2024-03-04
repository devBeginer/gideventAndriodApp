package ru.gidevent.androidapp.ui.state

sealed class UIState {
    object Loading: UIState()
    object ConnectionError: UIState()
    object Idle: UIState()
    object Unauthorised: UIState()
    class Error(val message: String): UIState()
    class Success<T>(val data: T): UIState()
}