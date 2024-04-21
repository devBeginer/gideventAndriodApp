package ru.gidevent.androidapp.ui.profileScreen

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.model.advertisement.response.ResponsePoster
import ru.gidevent.androidapp.data.model.auth.UserRoles
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.SellerRequest
import ru.gidevent.androidapp.data.model.auth.response.EditProfile
import ru.gidevent.androidapp.data.model.auth.response.ProfileResponse
import ru.gidevent.androidapp.data.model.auth.response.Seller
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.login.fragment.SignInScreenMode
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.ui.state.UIStateEditProfile
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertisementRepository: AdvertisementRepository) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIStateEditProfile?>()
    val data: LiveData<UIStateEditProfile?>
        get() = dataResultMutableLiveData


    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            dataResultMutableLiveData.postValue(UIStateEditProfile.Loading)
            val response = repository.getEditUserInfo()
            when(response){
                is ApiResult.Success<EditProfile?> -> {
                    dataResultMutableLiveData.postValue(UIStateEditProfile.Success(response.data))
                }
                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(UIStateEditProfile.ConnectionError)
                        response.code == 404 -> dataResultMutableLiveData.postValue(UIStateEditProfile.Error("Произошла ошибка, попробуйте снова"))
                        else -> dataResultMutableLiveData.postValue(UIStateEditProfile.Error(response.body))
                    }
                }
            }
        }
    }


    fun update(login: String, password: String, firstName: String, lastName: String, about: String, roles: Set<UserRoles>, photo: String){
        viewModelScope.launch (Dispatchers.IO){

            dataResultMutableLiveData.postValue(UIStateEditProfile.Loading)
            val response =
                repository.updateUser(EditProfile(0, photo, firstName, lastName, about, login, password, roles))
            when(response){
                is ApiResult.Success<ProfileResponse?> -> {
                    response.data?.let{
                        val loginResponse = repository.userSignIn(LoginBodyRequest(it.login, password))

                        if(loginResponse is ApiResult.Success<LoginBodyResponse>){
                            repository.saveCredentialsToSP(loginResponse.data)
                        }
                    }
                    dataResultMutableLiveData.postValue(UIStateEditProfile.SuccessUpdate)
                }
                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(UIStateEditProfile.ConnectionError)
                        else -> dataResultMutableLiveData.postValue(UIStateEditProfile.Error(response.body))
                    }
                }
            }
        }
    }

    suspend fun postPhoto(uri: Uri): UIState {
        val response = advertisementRepository.insertPoster(uri)

        return when (response) {
            is ApiResult.Success<ResponsePoster?> -> {
                UIState.Success(response.data)
            }

            is ApiResult.Error -> {
                when {
                    response.body.contains("Connection") -> UIState.ConnectionError
                    response.code == 404 -> UIState.Error("Произошла ошибка, попробуйте снова")
                    else -> UIState.Error(response.body)
                }
            }
        }

    }
}