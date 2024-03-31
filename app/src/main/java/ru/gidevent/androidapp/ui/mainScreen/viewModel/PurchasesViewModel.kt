package ru.gidevent.androidapp.ui.mainScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIStateAdvertList>(UIStateAdvertList.Idle)
    val data: LiveData<UIStateAdvertList>
        get() = dataResultMutableLiveData


    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            if(repository.isAuthorised()){
                dataResultMutableLiveData.postValue(UIStateAdvertList.Loading)
                val response = advertRepository.getFavouriteAdvertisement()
                when (response) {
                    is ApiResult.Success<List<Advertisement>> -> {

                        val mainDataSet = response.data.map { advertisement ->
                            AdvertPreviewCard(
                                advertisement.id,
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

                        dataResultMutableLiveData.postValue(UIStateAdvertList.Success(mainDataSet))
                    }

                    is ApiResult.Error -> {
                        when{
                            response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                                UIStateAdvertList.ConnectionError)
                            response.code == 403 || response.code == 401 -> dataResultMutableLiveData.postValue(UIStateAdvertList.Unauthorised)
                            response.code == 404 -> dataResultMutableLiveData.postValue(UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                        }
                    }
                }
            }else{
                dataResultMutableLiveData.postValue(UIStateAdvertList.Unauthorised)
            }
        }
    }

    fun postFavourite(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            dataResultMutableLiveData.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.postFavourite(id)
            when (response) {
                is ApiResult.Success<Advertisement> -> {

                    val advert = AdvertPreviewCard(
                        response.data.id,
                        response.data.favourite ?: false,
                        response.data.name,
                        listOf(response.data.category.name),
                        response.data.priceList?.let { ticketPriceList ->
                            ticketPriceList.firstOrNull()?.let { ticketPrice ->
                                ticketPrice.price
                            } ?: 0
                        } ?: 0,
                        response.data.photos.split(",").first()
                    )


                    dataResultMutableLiveData.postValue(UIStateAdvertList.Update(advert))
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.ConnectionError)
                        response.code == 403 || response.code == 401 -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.Unauthorised)
                        response.code == 404 -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }
}