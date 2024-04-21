package ru.gidevent.androidapp.ui.profileScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.androidapp.data.model.auth.response.Seller
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertisementRepository: AdvertisementRepository) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<Seller?>()
    val data: LiveData<Seller?>
        get() = dataResultMutableLiveData


    fun initView(id: Long){
        viewModelScope.launch (Dispatchers.IO){
            val response = advertisementRepository.getSellerInfo(id)
            when(response){
                is ApiResult.Success<Seller?> -> dataResultMutableLiveData.postValue(response.data)
                is ApiResult.Error -> dataResultMutableLiveData.postValue(null)
            }
        }
    }
}