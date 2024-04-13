package ru.gidevent.androidapp.ui.mainScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.myAdverts.BookingCardResponse
import ru.gidevent.androidapp.data.model.myAdverts.BookingInfoResponse
import ru.gidevent.androidapp.data.model.myAdverts.MyBooking
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIStateAdvertList>(UIStateAdvertList.Idle)
    val data: LiveData<UIStateAdvertList>
        get() = dataResultMutableLiveData



    private val _bottomSheetData = MutableLiveData<UIStateAdvertList>(UIStateAdvertList.Idle)
    val bottomSheetData: LiveData<UIStateAdvertList>
        get() = _bottomSheetData

    var id: Long? = null
    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            if(repository.isAuthorised()){
                dataResultMutableLiveData.postValue(UIStateAdvertList.Loading)
                val response = advertRepository.getPurchasesAdvertisement()
                when (response) {
                    is ApiResult.Success<List<BookingCardResponse>> -> {



                        val mainDataSet = response.data.map { bookingResponse ->
                            val eventTime = Calendar.getInstance(Locale.getDefault())
                            eventTime.timeInMillis = bookingResponse.eventTime
                            val date = Calendar.getInstance(Locale.getDefault())
                            date.timeInMillis = bookingResponse.date
                            date.set(Calendar.HOUR_OF_DAY, eventTime.get(Calendar.HOUR_OF_DAY))
                            date.set(Calendar.MINUTE, eventTime.get(Calendar.MINUTE))
                            date.set(Calendar.SECOND, eventTime.get(Calendar.SECOND))
                            date.set(Calendar.MILLISECOND, eventTime.get(Calendar.MILLISECOND))
                            MyBooking(
                                bookingResponse.id,
                                bookingResponse.advertisement,
                                bookingResponse.customerCount,
                                date,
                                bookingResponse.totalPrice,
                                bookingResponse.isApproved
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

    fun initBottomSheet(bookingId: Long){
        id = bookingId
        viewModelScope.launch (Dispatchers.IO){
            if(repository.isAuthorised()){
                _bottomSheetData.postValue(UIStateAdvertList.Loading)
                val response = advertRepository.getBookingInfo(bookingId)
                when (response) {
                    is ApiResult.Success<BookingInfoResponse> -> {

                        val mainData = response.data



                        _bottomSheetData.postValue(UIStateAdvertList.Success(mainData))
                    }

                    is ApiResult.Error -> {
                        when{
                            response.body.contains("Connection") -> _bottomSheetData.postValue(
                                UIStateAdvertList.ConnectionError)
                            response.code == 403 || response.code == 401 -> _bottomSheetData.postValue(UIStateAdvertList.Unauthorised)
                            response.code == 404 -> _bottomSheetData.postValue(UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                        }
                    }
                }
            }else{
                _bottomSheetData.postValue(UIStateAdvertList.Unauthorised)
            }
        }
    }
}