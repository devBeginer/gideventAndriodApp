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
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.HeaderViewpagerItem
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.MainRecyclerViewData
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<MainRecyclerViewData?>()
    val data: LiveData<MainRecyclerViewData?>
        get() = dataResultMutableLiveData


    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            val response = advertRepository.getTopAdvertisement()
            when(response){
                is ApiResult.Success<TopsResponse?> -> {
                    val headerDataSet = response.data?.let{
                        it.top.map { advertisement ->
                            HeaderViewpagerItem(
                                advertisement.name,
                                advertisement.priceList.firstOrNull()?.let { ticketPrice->ticketPrice.price }?:0,
                                advertisement.photos.split(",").first()
                            )
                        }
                    } ?: listOf<HeaderViewpagerItem>()
                    val mainDataSet = response.data?.let{
                        it.general.map { advertisement ->
                            AdvertPreviewCard(
                                false,
                                advertisement.name,
                                listOf(advertisement.category.name),
                                advertisement.priceList.firstOrNull()?.let { ticketPrice->ticketPrice.price }?:0,
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
}