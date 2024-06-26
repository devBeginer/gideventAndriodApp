package ru.gidevent.androidapp.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.SellerRequest
import ru.gidevent.androidapp.data.model.auth.response.RegisterBodyResponse
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.login.fragment.SignInScreenMode
import ru.gidevent.androidapp.ui.state.UIState
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    private val _registerState = MutableLiveData<UIState>(UIState.Idle)
    val registerState: LiveData<UIState>
        get() = _registerState


    fun register(login: String, password: String, firstName: String, lastName: String, about: String, role: SignInScreenMode){
        viewModelScope.launch (Dispatchers.IO){

            _registerState.postValue(UIState.Loading)
            val response =
                when(role){
                    SignInScreenMode.SELLER->repository.userSignUpAsSeller(SellerRequest(0, login, password, firstName, lastName, "", about))
                    SignInScreenMode.TOURIST->repository.userSignUp(RegisterBodyRequest(login, password, firstName, lastName, "User"))
            }
            when(response){
                is ApiResult.Success<*> -> {
                    _registerState.postValue(UIState.Success(true))
                }
                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _registerState.postValue(UIState.ConnectionError)
                        else -> _registerState.postValue(UIState.Error(response.body))
                    }
                }
            }
        }
    }
}
