package ru.gidevent.androidapp.ui.admin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.androidapp.data.model.myAdverts.MyAdvert
import ru.gidevent.androidapp.data.model.myAdverts.SellerAdvertResponse
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import javax.inject.Inject

@HiltViewModel
class ModerateAdvertsViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val _data = MutableLiveData<UIStateAdvertList>(UIStateAdvertList.Idle)
    val data: LiveData<UIStateAdvertList>
        get() = _data


    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            if(repository.isAuthorised()){
                _data.postValue(UIStateAdvertList.Loading)
                val response = advertRepository.getAdminAdvert()
                when (response) {
                    is ApiResult.Success<List<SellerAdvertResponse>> -> {

                        val mainDataSet = response.data.map { advertisement ->
                            MyAdvert(
                                advertisement.id,
                                advertisement.advertisement,
                                advertisement.customerCount,
                                advertisement.totalPrice,
                                advertisement.status
                            )
                        }

                        _data.postValue(UIStateAdvertList.Success(mainDataSet))
                    }

                    is ApiResult.Error -> {
                        when{
                            response.body.contains("Connection") -> _data.postValue(
                                UIStateAdvertList.ConnectionError)
                            response.code == 403 || response.code == 401 -> _data.postValue(UIStateAdvertList.Unauthorised)
                            response.code == 404 -> _data.postValue(UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                        }
                    }
                }
            }else{
                _data.postValue(UIStateAdvertList.Unauthorised)
            }
        }
    }

    fun confirm(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _data.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.confirmAdvertModeration(id)
            when (response) {
                is ApiResult.Success<Boolean> -> {

                    /*val advert = AdvertPreviewCard(
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


                    _data.postValue(UIStateAdvertList.Update(advert))*/
                    initView()
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _data.postValue(
                            UIStateAdvertList.ConnectionError)
                        response.code == 403 || response.code == 401 -> _data.postValue(
                            UIStateAdvertList.Unauthorised)
                        response.code == 404 -> _data.postValue(
                            UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }

    fun delete(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _data.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.deleteAdvertisement(id)
            when (response) {
                is ApiResult.Success<Boolean> -> {
                    initView()
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _data.postValue(
                            UIStateAdvertList.ConnectionError)
                        response.code == 403 || response.code == 401 -> _data.postValue(
                            UIStateAdvertList.Unauthorised)
                        response.code == 404 -> _data.postValue(
                            UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }

    fun decline(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _data.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.declineAdvertModeration(id)
            when (response) {
                is ApiResult.Success<Boolean> -> {

                    /*val advert = AdvertPreviewCard(
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


                    _data.postValue(UIStateAdvertList.Update(advert))*/
                    initView()
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _data.postValue(
                            UIStateAdvertList.ConnectionError)
                        response.code == 403 || response.code == 401 -> _data.postValue(
                            UIStateAdvertList.Unauthorised)
                        response.code == 404 -> _data.postValue(
                            UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }
}