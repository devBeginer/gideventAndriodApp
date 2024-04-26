package ru.gidevent.androidapp.ui.admin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import javax.inject.Inject

@HiltViewModel
class AdminCustomerViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val advertRepository: AdvertisementRepository
): ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIState>(UIState.Idle)
    val data: LiveData<UIState>
        get() = dataResultMutableLiveData


    fun initCustomer() {
        viewModelScope.launch {
            dataResultMutableLiveData.postValue(UIState.Loading)
            val response = advertRepository.getAllCustomerCategory()

            when (response) {
                is ApiResult.Success<List<CustomerCategory>> -> {
                    dataResultMutableLiveData.postValue(
                        UIState.Success(
                            response.data
                        )
                    )
                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(UIState.ConnectionError)
                        response.code == 404 -> dataResultMutableLiveData.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }


    fun deleteCustomerCategory(id: Long) {
        viewModelScope.launch(Dispatchers.IO){
            dataResultMutableLiveData.postValue(UIState.Loading)
            val response = advertRepository.deleteCustomerCategory(id)
            when (response) {
                is ApiResult.Success<Boolean> -> {
                    initCustomer()
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                            UIState.ConnectionError)
                        response.code == 403 || response.code == 401 -> dataResultMutableLiveData.postValue(
                            UIState.Unauthorised)
                        response.code == 404 -> dataResultMutableLiveData.postValue(
                            UIState.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }
    suspend fun addCustomerCategory(category: String): UIState {
        val response = advertRepository.postCustomerCategory(CustomerCategory(0, category))

        return when (response) {
            is ApiResult.Success<CustomerCategory> -> {
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

    suspend fun editCustomerCategory(customerCategory: CustomerCategory): UIState {
        val response = advertRepository.putCustomerCategory(customerCategory)

        return when (response) {
            is ApiResult.Success<CustomerCategory> -> {
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