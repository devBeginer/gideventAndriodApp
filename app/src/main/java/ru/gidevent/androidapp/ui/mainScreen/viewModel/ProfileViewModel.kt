package ru.gidevent.androidapp.ui.mainScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UserDetailsResponse?>()
    val data: LiveData<UserDetailsResponse?>
        get() = dataResultMutableLiveData


    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            val response = repository.getUserById()
            when(response){
                is ApiResult.Success<UserDetailsResponse?> -> dataResultMutableLiveData.postValue(response.data)
                is ApiResult.Error -> dataResultMutableLiveData.postValue(null)
            }
        }
    }

    fun logout(){
        repository.logout()
    }
}