package ru.gidevent.androidapp.ui.login.viewModel

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
class SignInViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    /*private val loginResultMutableLiveData = MutableLiveData<Boolean>(false)
    val loginResult: LiveData<Boolean>
        get() = loginResultMutableLiveData*/

    private val _loginState = MutableLiveData<UIState>(UIState.Idle)
    val loginState: LiveData<UIState>
        get() = _loginState


    fun login(login: String, password: String){
        viewModelScope.launch (Dispatchers.IO){
            val response = repository.userSignIn(LoginBodyRequest(login, password))
            when(response){
                is ApiResult.Success<LoginBodyResponse> -> {
                    /*repository.saveAccessTokenToSP(response.data.accessToken)
                    repository.saveRefreshTokenToSP(response.data.refreshToken)*/
                    repository.saveCredentialsToSP(response.data)
                    _loginState.postValue(UIState.Success(true))
                    //loginResultMutableLiveData.postValue(true)
                }
                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _loginState.postValue(UIState.ConnectionError)
                        response.code == 403 || response.code == 401 -> _loginState.postValue(UIState.Error("Проверьте логин и пароль, и попробуйте снова"))
                    }
                    //loginResultMutableLiveData.postValue(false)
                }
            }
        }
    }
}