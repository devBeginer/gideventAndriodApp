package ru.gidevent.androidapp.ui.VKID

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import javax.inject.Inject

@HiltViewModel
class VkAuthViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {


    private val _loginState = MutableLiveData<UIState>(UIState.Idle)
    val loginState: LiveData<UIState>
        get() = _loginState


    fun login(accessToken: String, uuid: String){
        viewModelScope.launch (Dispatchers.IO){

            _loginState.postValue(UIState.Loading)
            val response = repository.userSignIn(accessToken, uuid)
            when(response){
                is ApiResult.Success<LoginBodyResponse> -> {
                    repository.saveCredentialsToSP(response.data)
                    _loginState.postValue(UIState.Success(true))
                }
                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _loginState.postValue(UIState.ConnectionError)
                        response.code == 403 || response.code == 401 -> _loginState.postValue(UIState.Error("Проверьте логин и пароль, и попробуйте снова"))
                    }
                }
            }
        }
    }
}