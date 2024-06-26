package ru.gidevent.androidapp.ui.advertisement

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
import ru.gidevent.androidapp.data.model.advertisement.dto.NewFeedback
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPriceRequest
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.advertisement.response.AdvertisementExpanded
import ru.gidevent.androidapp.data.model.advertisement.response.NewFeedbackResponse
import ru.gidevent.androidapp.data.model.advertisement.response.TicketPriceResponse
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdvertisementViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIState>(UIState.Idle)
    val data: LiveData<UIState>
        get() = dataResultMutableLiveData


    private val _feedbackData = MutableLiveData<NewFeedbackResponse>(null)
    val feedbackData: LiveData<NewFeedbackResponse>
        get() = _feedbackData

    private val favouriteMutableLiveData = MutableLiveData<AdvertPreviewCard?>()
    val favourite: LiveData<AdvertPreviewCard?>
        get() = favouriteMutableLiveData

    var advertId: Long? = null

    fun initView(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            advertId = id
            dataResultMutableLiveData.postValue(UIState.Loading)
            val response = advertRepository.getAdvertisementById(id)
            when (response) {
                is ApiResult.Success<AdvertisementExpanded> -> {

                    val mainData = response.data

                    dataResultMutableLiveData.postValue(
                        UIState.Success(
                            AdvertisementCardInfo(
                                mainData.id,
                                mainData.name,
                                mainData.duration,
                                mainData.description,
                                mainData.transportation,
                                mainData.ageRestrictions,
                                mainData.visitorsCount,
                                mainData.isIndividual,
                                mainData.rating,
                                mainData.category,
                                mainData.city,
                                mainData.favourite,
                                mainData.seller,
                                mainData.priceList,
                                if(mainData.reviewsList!=null) mainData.reviewsList.map { ReviewRecyclerViewItem(it.rating, it.text, it.userName, it.avatarUrl) } else listOf(),
                                mainData.eventTimeList?.map {eventTimeResponse ->
                                    val time = Calendar.getInstance(Locale.getDefault())
                                    time.timeInMillis = eventTimeResponse.time
                                    val dateStart = Calendar.getInstance(Locale.getDefault())
                                    dateStart.timeInMillis = eventTimeResponse.startDate
                                    val dateEnd = Calendar.getInstance(Locale.getDefault())
                                    dateEnd.timeInMillis = eventTimeResponse.endDate
                                    EventTime(eventTimeResponse.timeId, time, eventTimeResponse.isRepeatable, eventTimeResponse.daysOfWeek, dateStart, dateEnd)
                                },
                                mainData.photos.split(",").map { AdvertViewpagerItem(it) }
                            )
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

    fun initView() {
        viewModelScope.launch(Dispatchers.IO) {

            val id = advertId
            if(id!=null){
                dataResultMutableLiveData.postValue(UIState.Loading)
                val response = advertRepository.getAdvertisementById(id)
                when (response) {
                    is ApiResult.Success<AdvertisementExpanded> -> {

                        val mainData = response.data

                        dataResultMutableLiveData.postValue(
                            UIState.Success(
                                AdvertisementCardInfo(
                                    mainData.id,
                                    mainData.name,
                                    mainData.duration,
                                    mainData.description,
                                    mainData.transportation,
                                    mainData.ageRestrictions,
                                    mainData.visitorsCount,
                                    mainData.isIndividual,
                                    mainData.rating,
                                    mainData.category,
                                    mainData.city,
                                    mainData.favourite,
                                    mainData.seller,
                                    mainData.priceList,
                                    if(mainData.reviewsList!=null) mainData.reviewsList.map { ReviewRecyclerViewItem(it.rating, it.text, it.userName, it.avatarUrl) } else listOf(),
                                    mainData.eventTimeList?.map {eventTimeResponse ->
                                        val time = Calendar.getInstance(Locale.getDefault())
                                        time.timeInMillis = eventTimeResponse.time
                                        val dateStart = Calendar.getInstance(Locale.getDefault())
                                        dateStart.timeInMillis = eventTimeResponse.startDate
                                        val dateEnd = Calendar.getInstance(Locale.getDefault())
                                        dateEnd.timeInMillis = eventTimeResponse.endDate
                                        EventTime(eventTimeResponse.timeId, time, eventTimeResponse.isRepeatable, eventTimeResponse.daysOfWeek, dateStart, dateEnd)
                                    },
                                    mainData.photos.split(",").map { AdvertViewpagerItem(it) }
                                )
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

    suspend fun postFeedback(newFeedback: NewFeedback): UIState {
        val id = advertId

        return if(id!=null){
            val response = advertRepository.postFeedback(NewFeedback(id, newFeedback.rating, newFeedback.text))

            when (response) {
                is ApiResult.Success<NewFeedbackResponse> -> {
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

    fun initFeedback() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = advertId

            if(id!=null){
                val response = advertRepository.getFeedback(id)

                when (response) {
                    is ApiResult.Success<NewFeedbackResponse> -> {
                        _feedbackData.postValue(response.data)
                    }

                    is ApiResult.Error -> {
                        when {
                            response.body.contains("Connection") -> dataResultMutableLiveData.postValue(UIState.ConnectionError)
                            /*response.code == 404 -> dataResultMutableLiveData.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                            else -> dataResultMutableLiveData.postValue(UIState.Error(response.body))*/
                        }
                    }
                }
            }else{
                dataResultMutableLiveData.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
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

