package ru.gidevent.androidapp.ui.makeBooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.androidapp.data.model.advertisement.AdvertViewpagerItem
import ru.gidevent.androidapp.data.model.advertisement.AdvertisementCardInfo
import ru.gidevent.androidapp.data.model.advertisement.ReviewRecyclerViewItem
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.data.model.advertisement.response.AdvertisementExpanded
import ru.gidevent.androidapp.data.model.booking.BookingParams
import ru.gidevent.androidapp.data.model.booking.BookingParamsResponse
import ru.gidevent.androidapp.data.model.booking.BookingPriceRVItem
import ru.gidevent.androidapp.data.model.booking.BookingRequest
import ru.gidevent.androidapp.data.model.booking.BookingResponse
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.ui.state.UIStateMakeBooking
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MakeBookingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val advertRepository: AdvertisementRepository
): ViewModel() {

    var date = Calendar.getInstance(Locale.getDefault())
    init {
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.MILLISECOND, 0)
    }

    private val _bookingVariant = MutableLiveData<UIStateMakeBooking>(UIStateMakeBooking.Loading)
    val bookingVariant: LiveData<UIStateMakeBooking>
        get() = _bookingVariant

    private val _total = MutableLiveData<Pair<Int, Int>>(Pair(0, 0))
    val total: LiveData<Pair<Int, Int>>
        get() = _total

    val priceCount = mutableMapOf<Long, Int>()
    var maxCount = mutableMapOf<Long, Int>()
    var totalCount = 0
    var totalCost = 0

    fun initView(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _bookingVariant.postValue(UIStateMakeBooking.Loading)
            val response = advertRepository.getBookingParams(id, date.timeInMillis)
            when (response) {
                is ApiResult.Success<BookingParamsResponse> -> {

                    val mainData = response.data
                    maxCount.clear()
                    priceCount.clear()
                    totalCount = 0
                    totalCost = 0
                    val eventTime = mainData.eventTimeList.map {
                        maxCount[it.timeId] = it.count
                        val time = Calendar.getInstance(Locale.getDefault())
                        time.timeInMillis = it.time
                        val startData = Calendar.getInstance(Locale.getDefault())
                        startData.timeInMillis = it.startDate
                        val endData = Calendar.getInstance(Locale.getDefault())
                        endData.timeInMillis = it.endDate
                        EventTime(it.timeId, time, it.isRepeatable, it.daysOfWeek, startData, endData)
                    }
                    val price = if(eventTime.isNotEmpty()) {
                        mainData.price.map {
                            BookingPriceRVItem(
                                it.priceId,
                                it.customerCategory.customerCategoryId,
                                it.customerCategory.name,
                                it.price,
                                priceCount[it.priceId] ?: 0
                            )
                        }
                    }else{
                        listOf()
                    }


                    _bookingVariant.postValue(
                        UIStateMakeBooking.UpdateUI(
                            BookingParams(eventTime, price)
                        )
                    )
                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> _bookingVariant.postValue(
                            UIStateMakeBooking.ConnectionError
                        )

                        response.code == 403 || response.code == 401 -> _bookingVariant.postValue(
                            UIStateMakeBooking.Unauthorised
                        )

                        response.code == 404 -> _bookingVariant.postValue(UIStateMakeBooking.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }

        }
    }

    fun postBooking(bookingRequest: BookingRequest){
        viewModelScope.launch(Dispatchers.IO) {
            _bookingVariant.postValue(UIStateMakeBooking.Loading)
            val response = advertRepository.postBooking(bookingRequest)
            when (response) {
                is ApiResult.Success<BookingResponse> -> {

                    val mainData = response.data

                    _bookingVariant.postValue(
                        UIStateMakeBooking.SuccessBook(
                            true
                        )
                    )
                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> _bookingVariant.postValue(
                            UIStateMakeBooking.ConnectionError
                        )

                        response.code == 403 || response.code == 401 -> _bookingVariant.postValue(
                            UIStateMakeBooking.Unauthorised
                        )

                        response.code == 404 -> _bookingVariant.postValue(UIStateMakeBooking.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }

        }
    }

    fun postTotal(){
        _total.postValue(Pair(totalCount, totalCost))
    }

    fun resetPriceList(){
        priceCount.clear()
        totalCount = 0
        totalCost = 0
    }

}