package ru.gidevent.androidapp.ui.admin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import javax.inject.Inject

@HiltViewModel
class AdminTransportViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val advertRepository: AdvertisementRepository
): ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIState>(UIState.Idle)
    val data: LiveData<UIState>
        get() = dataResultMutableLiveData


    fun initTransport() {
        viewModelScope.launch {
            dataResultMutableLiveData.postValue(UIState.Loading)
            val response = advertRepository.getAllTransport()

            when (response) {
                is ApiResult.Success<List<TransportationVariant>> -> {
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


    fun deleteTransportation(id: Long) {
        viewModelScope.launch(Dispatchers.IO){
            dataResultMutableLiveData.postValue(UIState.Loading)
            val response = advertRepository.deleteTransportation(id)
            when (response) {
                is ApiResult.Success<Boolean> -> {
                    initTransport()
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
    suspend fun addTransportation(transport: String): UIState {
        val response = advertRepository.postTransportationVariant(TransportationVariant(0, transport))

        return when (response) {
            is ApiResult.Success<TransportationVariant> -> {
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

    suspend fun editTransportation(transportationVariant: TransportationVariant): UIState {
        val response = advertRepository.putTransportationVariant(transportationVariant)

        return when (response) {
            is ApiResult.Success<TransportationVariant> -> {
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