package ru.gidevent.androidapp.ui.mainScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.response.TopsResponse
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.HeaderViewpagerItem
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.MainRecyclerViewData
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIState>(UIState.Idle)
    val data: LiveData<UIState>
        get() = dataResultMutableLiveData


    fun initView() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = advertRepository.getFavouriteAdvertisement()
            when (response) {
                is ApiResult.Success<List<Advertisement>> -> {

                    val mainDataSet = response.data.map { advertisement ->
                        AdvertPreviewCard(
                            advertisement.favourite ?: false,
                            advertisement.name,
                            listOf(advertisement.category.name),
                            advertisement.priceList?.let { ticketPriceList ->
                                ticketPriceList.firstOrNull()?.let { ticketPrice ->
                                    ticketPrice.price
                                } ?: 0
                            } ?: 0,
                            advertisement.photos.split(",").first()
                        )
                    }

                    dataResultMutableLiveData.postValue(UIState.Success(mainDataSet))
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(UIState.ConnectionError)
                        response.code == 404 -> dataResultMutableLiveData.postValue(UIState.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }
}