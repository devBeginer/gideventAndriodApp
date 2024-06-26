package ru.gidevent.androidapp.ui.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.City
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.androidapp.data.model.advertisement.PriceRVItem
import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.data.model.advertisement.request.EventTimeRequest
import ru.gidevent.androidapp.data.model.advertisement.request.NewAdvertisement
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPriceRequest
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.advertisement.response.AdvertisementEdit
import ru.gidevent.androidapp.data.model.advertisement.response.EventTimeResponse
import ru.gidevent.androidapp.data.model.advertisement.response.ResponsePoster
import ru.gidevent.androidapp.data.model.advertisement.response.TicketPriceResponse
import ru.gidevent.androidapp.data.model.search.OptionsVariants
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionCity
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateAdvertViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val advertRepository: AdvertisementRepository
): ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIState>(UIState.Idle)
    val data: LiveData<UIState>
        get() = dataResultMutableLiveData

    private val _scheduleData = MutableLiveData<EventTime?>(null)
    val scheduleData: LiveData<EventTime?>
        get() = _scheduleData

    private val _ticketPriceDate = MutableLiveData<TicketPriceResponse?>(null)
    val ticketPriceDate: LiveData<TicketPriceResponse?>
        get() = _ticketPriceDate

    private val _postResult = MutableLiveData<UIState>(UIState.Idle)
    val postResult: LiveData<UIState>
        get() = _postResult

    private val _optionsVariants = MutableLiveData<UIState>(UIState.Idle)
    val optionsVariants: LiveData<UIState>
        get() = _optionsVariants

    private val _fields = MutableLiveData<UIState>(UIState.Idle)
    val fields: LiveData<UIState>
        get() = _fields

    //var city: City? = null

    private val _city = MutableLiveData<City?>(null)
    val city: LiveData<City?>
        get() = _city

    private val _citySuggestions = MutableLiveData<UIState>(UIState.Idle)
    val citySuggestions: LiveData<UIState>
        get() = _citySuggestions

    var editableStartDate: Calendar? = null
    var editableEndDate: Calendar? = null

    private val scheduleList: MutableList<EventTime> = mutableListOf()
    private val _eventTimeList = MutableLiveData<UIState>(UIState.Idle)
    val eventTimeList: LiveData<UIState>
        get() = _eventTimeList

    private val ticketPriceList: MutableList<TicketPriceResponse> = mutableListOf()
    private val _priceList = MutableLiveData<UIState>(UIState.Idle)
    val priceList: LiveData<UIState>
        get() = _priceList

    private val _customerVariants = MutableLiveData<UIState>(UIState.Idle)
    val customerVariants: LiveData<UIState>
        get() = _customerVariants

    var advertId: Long? = null
    fun postCity(city: City){
        _city.postValue(city)
    }



    suspend fun postPhoto(uri: Uri): UIState {
        val response = advertRepository.insertPoster(uri)

        return when (response) {
            is ApiResult.Success<ResponsePoster?> -> {
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



    fun delPrice(id: Long) {
        viewModelScope.launch(Dispatchers.IO){
            _priceList.postValue(UIState.Loading)
            val response = advertRepository.deleteTicketPrice(id)
            when (response) {
                is ApiResult.Success<Boolean> -> {
                    initPriceList()
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _priceList.postValue(
                            UIState.ConnectionError)
                        response.code == 403 || response.code == 401 -> _priceList.postValue(
                            UIState.Unauthorised)
                        response.code == 404 -> _priceList.postValue(
                            UIState.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }

    fun delTime(id: Long) {
        viewModelScope.launch(Dispatchers.IO){
            _eventTimeList.postValue(UIState.Loading)
            val response = advertRepository.deleteEventTime(id)
            when (response) {
                is ApiResult.Success<Boolean> -> {
                    initScheduleList()
                }

                is ApiResult.Error -> {
                    when{
                        response.body.contains("Connection") -> _eventTimeList.postValue(
                            UIState.ConnectionError)
                        response.code == 403 || response.code == 401 -> _eventTimeList.postValue(
                            UIState.Unauthorised)
                        response.code == 404 -> _eventTimeList.postValue(
                            UIState.Error("Произошла ошибка, и попробуйте снова"))
                    }
                }
            }
        }
    }

    fun initOptions(isEditMode: Boolean) {
        viewModelScope.launch {
            _optionsVariants.postValue(UIState.Loading)
            val response = advertRepository.getOptionsVariants()

            when (response) {
                is ApiResult.Success<OptionsVariants?> -> {
                    _optionsVariants.postValue(
                        UIState.Success(
                            response.data
                        )
                    )
                    if(isEditMode) initFields()
                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> _optionsVariants.postValue(UIState.ConnectionError)
                        response.code == 404 -> _optionsVariants.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }

    fun postAdvertisement(newAdvertisement: NewAdvertisement, isEditMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _postResult.postValue(UIState.Loading)
            val response = if(isEditMode){
                advertRepository.putAdvertisement(newAdvertisement)
            }else{
                advertRepository.postAdvertisement(newAdvertisement)
            }
            when (response) {
                is ApiResult.Success<Advertisement?> -> {
                    advertId = response.data?.id
                    _postResult.postValue(UIState.Success(true))
                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> _postResult.postValue(UIState.ConnectionError)
                        response.code == 404 -> _postResult.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }

    fun getCitySuggestions(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _citySuggestions.postValue(UIState.Loading)
            val response = advertRepository.getCitySuggestions(query)


            when (response) {
                is ApiResult.Success<List<CitySuggestion>> -> {
                    val data = response.data.map {
                        SuggestionCity(it.id, it.name)
                    }

                    _citySuggestions.postValue(UIState.Success(data))


                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> _citySuggestions.postValue(UIState.ConnectionError)
                        response.code == 404 -> _citySuggestions.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }

    fun initFields() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = advertId
            if(id!=null){
                dataResultMutableLiveData.postValue(UIState.Loading)
                val response = advertRepository.getAdvertisementForEdit(id)
                when (response) {
                    is ApiResult.Success<AdvertisementEdit> -> {

                        val mainData = response.data
                        _city.postValue(mainData.city)

                        dataResultMutableLiveData.postValue(
                            UIState.Success(
                                mainData
                            )
                        )
                    }

                    is ApiResult.Error -> {
                        when {
                            response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                                UIState.ConnectionError
                            )

                            response.code == 403 || response.code == 401 -> dataResultMutableLiveData.postValue(
                                UIState.Unauthorised
                            )

                            response.code == 404 -> dataResultMutableLiveData.postValue(UIState.Error("Произошла ошибка, и попробуйте снова"))
                        }
                    }
                }
            }
        }
    }

    fun initEventTimeFields(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            val data = scheduleList.find { it.timeId == id }
            _scheduleData.postValue(data)

        }
    }

    fun initPriceFields(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            val data = ticketPriceList.find { it.priceId == id }
            _ticketPriceDate.postValue(data)

        }
    }

    suspend fun addTransportations(transportation: String): UIState {
        val response = advertRepository.postTransportationVariant(TransportationVariant(0, transportation))

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

    suspend fun addCategory(category: String): UIState {
        val response = advertRepository.postCategory(Category(0, category))

        return when (response) {
            is ApiResult.Success<Category> -> {
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



    suspend fun createEventTime(eventTime: EventTime, isEditMode: Boolean): UIState {

        val id = advertId
        return if(id!=null){
            val response = if(isEditMode){
                advertRepository.putEventTime(EventTimeRequest(eventTime.timeId, id, eventTime.time.timeInMillis, eventTime.isRepeatable, eventTime.daysOfWeek, eventTime.startDate.timeInMillis, eventTime.endDate.timeInMillis))
            }else{
                advertRepository.postEventTime(EventTimeRequest(eventTime.timeId, id, eventTime.time.timeInMillis, eventTime.isRepeatable, eventTime.daysOfWeek, eventTime.startDate.timeInMillis, eventTime.endDate.timeInMillis))
            }

            when (response) {
                is ApiResult.Success<EventTimeResponse> -> {
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
        }else{
            UIState.Error("Произошла ошибка, попробуйте снова")
        }

    }

    fun initScheduleList() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = advertId
            if(id!=null){
                _eventTimeList.postValue(UIState.Loading)
                val response = advertRepository.getAllEventTime(id)


                when (response) {
                    is ApiResult.Success<List<EventTimeResponse>> -> {
                        val data = response.data.map {eventTimeResponse ->
                            val time = Calendar.getInstance(Locale.getDefault())
                            time.timeInMillis = eventTimeResponse.time
                            val dateStart = Calendar.getInstance(Locale.getDefault())
                            dateStart.timeInMillis = eventTimeResponse.startDate
                            val dateEnd = Calendar.getInstance(Locale.getDefault())
                            dateEnd.timeInMillis = eventTimeResponse.endDate
                            EventTime(eventTimeResponse.timeId, time, eventTimeResponse.isRepeatable, eventTimeResponse.daysOfWeek, dateStart, dateEnd)
                        }

                        scheduleList.clear()
                        scheduleList.addAll(data)
                        _eventTimeList.postValue(UIState.Success(data))


                    }

                    is ApiResult.Error -> {
                        when {
                            response.body.contains("Connection") -> _eventTimeList.postValue(UIState.ConnectionError)
                            response.code == 404 -> _eventTimeList.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                        }
                    }
                }
            }

        }
    }



    fun initPriceList() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = advertId
            if(id!=null){
                _priceList.postValue(UIState.Loading)
                val response = advertRepository.getAllTicketPrice(id)


                when (response) {
                    is ApiResult.Success<List<TicketPriceResponse>> -> {
                        val data = response.data.map {ticketPriceResponse ->
                            PriceRVItem(ticketPriceResponse.priceId, ticketPriceResponse.customerCategory.customerCategoryId, ticketPriceResponse.customerCategory.name, ticketPriceResponse.price)
                        }

                        ticketPriceList.clear()
                        ticketPriceList.addAll(response.data)
                        _priceList.postValue(UIState.Success(data))


                    }

                    is ApiResult.Error -> {
                        when {
                            response.body.contains("Connection") -> _priceList.postValue(UIState.ConnectionError)
                            response.code == 404 -> _priceList.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                        }
                    }
                }
            }

        }
    }

    suspend fun createPrice(price: Int, customerCategory: Long, isEditMode: Boolean, priceId: Long?): UIState {
        val id = advertId
        return if(id!=null){
            val response = if(isEditMode && priceId!=null){
                advertRepository.putTicketPrice(TicketPriceRequest(priceId, id, customerCategory, price))
            }else{
                advertRepository.postTicketPrice(TicketPriceRequest(0, id, customerCategory, price))
            }

            when (response) {
                is ApiResult.Success<TicketPriceResponse> -> {
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
        }else{
            UIState.Error("Произошла ошибка, попробуйте снова")
        }

    }

    fun initCustomerCategories(id: Long?) {
        viewModelScope.launch {
            _customerVariants.postValue(UIState.Loading)
            val response = advertRepository.getAllCustomerCategory()

            when (response) {
                is ApiResult.Success<List<CustomerCategory>> -> {
                    _customerVariants.postValue(
                        UIState.Success(
                            response.data
                        )
                    )
                    if (id != null) {
                        initPriceFields(id)
                    }
                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> _customerVariants.postValue(UIState.ConnectionError)
                        response.code == 404 -> _customerVariants.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }


}