package ru.gidevent.androidapp.ui.seller_management.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.androidapp.data.model.booking.BookingResponse
import ru.gidevent.androidapp.data.model.myAdverts.AdvertChip
import ru.gidevent.androidapp.data.model.myAdverts.BookingInfoResponse
import ru.gidevent.androidapp.data.model.myAdverts.MyBooking
import ru.gidevent.androidapp.data.model.myAdverts.BookingCardResponse
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val _data = MutableLiveData<UIStateAdvertList>(UIStateAdvertList.Idle)
    val data: LiveData<UIStateAdvertList>
        get() = _data


    private val _bottomSheetData = MutableLiveData<UIStateAdvertList>(UIStateAdvertList.Idle)
    val bottomSheetData: LiveData<UIStateAdvertList>
        get() = _bottomSheetData

    var date: Calendar? = null
    var id: Long? = null
    var advert: Long = -1L
    val adverts = mutableMapOf<Int, Long>()
    private val _advertsData = MutableLiveData<List<AdvertChip>>(listOf())
    val advertsData: LiveData<List<AdvertChip>>
        get() = _advertsData

    fun initView(){
        viewModelScope.launch (Dispatchers.IO){
            if(repository.isAuthorised()){
                _data.postValue(UIStateAdvertList.Loading)
                val response = advertRepository.getSellerBooking(advert, date?.timeInMillis)
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

    fun initChips(){
        viewModelScope.launch (Dispatchers.IO){

            if(repository.isAuthorised()){
                _data.postValue(UIStateAdvertList.Loading)
                val response = advertRepository.getAdvertChips()
                when (response) {
                    is ApiResult.Success<List<AdvertChip>> -> {
                        _advertsData.postValue(response.data)
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

    fun confirmFromBottomSheet(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _bottomSheetData.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.postBookingConfirmation(id)
            when (response) {
                is ApiResult.Success<BookingResponse> -> {
                    if(response.data!=null){
                        _bottomSheetData.postValue(UIStateAdvertList.Update(response.data.isApproved))
                    }
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _bottomSheetData.postValue(
                            UIStateAdvertList.ConnectionError)
                        response.code == 403 || response.code == 401 -> _bottomSheetData.postValue(
                            UIStateAdvertList.Unauthorised)
                        response.code == 404 -> _bottomSheetData.postValue(
                            UIStateAdvertList.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }

    fun confirmFromFragment(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _data.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.postBookingConfirmation(id)
            when (response) {
                is ApiResult.Success<BookingResponse> -> {

                    if(response.data!=null){
                        _data.postValue(UIStateAdvertList.Update(Pair(response.data.id, response.data.isApproved)))
                    }
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