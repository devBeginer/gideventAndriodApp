package ru.gidevent.androidapp.ui.mainScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<MainRecyclerViewData?>()
    val data: LiveData<MainRecyclerViewData?>
        get() = dataResultMutableLiveData

    private val favouriteMutableLiveData = MutableLiveData<AdvertPreviewCard?>()
    val favourite: LiveData<AdvertPreviewCard?>
        get() = favouriteMutableLiveData



    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            val response = advertRepository.getTopAdvertisement()
            when(response){
                is ApiResult.Success<TopsResponse?> -> {
                    val headerDataSet = response.data?.let{
                        it.top.map { advertisement ->
                            HeaderViewpagerItem(
                                advertisement.id,
                                advertisement.name,
                                advertisement.priceList?.let { ticketPriceList ->
                                    ticketPriceList.firstOrNull()?.let { ticketPrice->
                                        ticketPrice.price
                                    }?:0
                                }?:0,
                                advertisement.photos.split(",").first()
                            )
                        }
                    } ?: listOf<HeaderViewpagerItem>()
                    val mainDataSet = response.data?.let{
                        it.general.map { advertisement ->
                            AdvertPreviewCard(
                                advertisement.id,
                                advertisement.favourite?:false,
                                advertisement.name,
                                listOf(advertisement.category.name),
                                advertisement.priceList?.let { ticketPriceList ->
                                    ticketPriceList.firstOrNull()?.let { ticketPrice->
                                        ticketPrice.price
                                    }?:0
                                }?:0,
                                advertisement.photos.split(",").first()
                            )
                        }
                    } ?: listOf<AdvertPreviewCard>()
                    val categories = response.data?.let{
                        it.categories.map { category: Category ->  category.name }
                    } ?: listOf<String>()

                    dataResultMutableLiveData.postValue(MainRecyclerViewData(headerDataSet, categories, mainDataSet))
                }
                is ApiResult.Error -> dataResultMutableLiveData.postValue(null)
            }
        }
    }

    fun postFavourite(id: Long){
        viewModelScope.launch(Dispatchers.IO){
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


                    favouriteMutableLiveData.postValue(advert)
                }

                is ApiResult.Error -> {
                    /*when{
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.ConnectionError)
                        response.code == 403 || response.code == 401 -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.Unauthorised)
                        response.code == 404 -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                    }*/


                    favouriteMutableLiveData.postValue(null)
                }
            }
        }
    }
}