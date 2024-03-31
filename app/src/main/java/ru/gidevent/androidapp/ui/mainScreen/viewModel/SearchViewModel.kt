package ru.gidevent.androidapp.ui.mainScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.RestAPI.model.response.Suggestions
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.request.search.SearchOptions
import ru.gidevent.androidapp.data.model.search.OptionsVariants
import ru.gidevent.androidapp.data.model.search.SearchOptionsDto
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionCategory
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionCity
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionItem
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData
import ru.gidevent.androidapp.data.repository.AdvertisementRepository
import ru.gidevent.androidapp.data.repository.UserRepository
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: UserRepository,
    private val advertRepository: AdvertisementRepository
) : ViewModel() {
    private val dataResultMutableLiveData = MutableLiveData<UIStateAdvertList>(UIStateAdvertList.Idle)
    val data: LiveData<UIStateAdvertList>
        get() = dataResultMutableLiveData
    var approvedSearchOptions: SearchOptionsDto = SearchOptionsDto(
        0f,
        50000f,
        null,
        null,
        mutableListOf(),
        true,
        true,
        0f,
        72f,
        mutableListOf(),
        null,
        null,
        0f,
        5f
    )
    private var defaultSearchOptions: SearchOptionsDto = SearchOptionsDto(
        0f,
        50000f,
        null,
        null,
        mutableListOf(),
        true,
        true,
        0f,
        72f,
        mutableListOf(),
        null,
        null,
        0f,
        5f
    )


    private val _searchOptions = MutableLiveData<SearchOptionsDto>(/*approvedSearchOptions*/)
    val searchOptions: LiveData<SearchOptionsDto>
        get() = _searchOptions


    private val _searchSuggestions = MutableLiveData<UIState>(UIState.Idle)
    val searchSuggestions: LiveData<UIState>
        get() = _searchSuggestions


    private val _citySuggestions = MutableLiveData<UIState>(UIState.Idle)
    val citySuggestions: LiveData<UIState>
        get() = _citySuggestions


    private val _optionsVariants = MutableLiveData<UIState>(UIState.Idle)
    val optionsVariants: LiveData<UIState>
        get() = _optionsVariants


    fun initView() {
        viewModelScope.launch(Dispatchers.IO) {

            dataResultMutableLiveData.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.getAllAdvertisement()


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
                    when {
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.ConnectionError
                        )

                        response.code == 404 -> dataResultMutableLiveData.postValue(UIStateAdvertList.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }


    fun searchByName(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataResultMutableLiveData.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.getSearchAdvertisement(query)


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
                    when {
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.ConnectionError
                        )

                        response.code == 404 -> dataResultMutableLiveData.postValue(UIStateAdvertList.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }

    fun getSuggestions(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = advertRepository.getSearchSuggestions(query)


            when (response) {
                is ApiResult.Success<Suggestions?> -> {
                    val advert = response.data?.advertList?.map { advertSuggestion ->
                        SuggestionItem(
                            advertSuggestion.id,
                            advertSuggestion.name,
                            advertSuggestion.city
                        )
                    }
                    val city = response.data?.cityList?.map { citySuggestion ->
                        SuggestionCity(citySuggestion.id, citySuggestion.name)
                    }
                    val category = response.data?.categoryList?.map { categorySuggestion ->
                        SuggestionCategory(categorySuggestion.id, categorySuggestion.name)
                    }

                    if (category != null && city != null && advert != null) {
                        _searchSuggestions.postValue(
                            UIState.Success(
                                SuggestionRecyclerViewData(
                                    category,
                                    city,
                                    advert
                                )
                            )
                        )
                    }

                }

                is ApiResult.Error -> {
                    when {
                        response.body.contains("Connection") -> _searchSuggestions.postValue(UIState.ConnectionError)
                        response.code == 404 -> _searchSuggestions.postValue(UIState.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
            }
        }
    }


    fun getCitySuggestions(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun initOptions() {
        viewModelScope.launch {
            val response = advertRepository.getOptionsVariants()

            when (response) {
                is ApiResult.Success<OptionsVariants?> -> {
                    _optionsVariants.postValue(
                        UIState.Success(
                            response.data
                        )

                    )

                    _searchOptions.postValue(approvedSearchOptions)
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

    fun postSearchOptions(options: SearchOptionsDto) {
        _searchOptions.postValue(options)
    }

    fun resetSearchOptions() {
        _searchOptions.postValue(defaultSearchOptions)
    }

    fun approveOptions() {
        searchOptions.value?.let {
            approvedSearchOptions = it
        }
    }


    fun searchByParams() {
        viewModelScope.launch(Dispatchers.IO) {
            dataResultMutableLiveData.postValue(UIStateAdvertList.Loading)
            val response = advertRepository.getAdvertisementByParams(
                SearchOptions(
                    if (approvedSearchOptions.priceFrom == 0f) null else approvedSearchOptions.priceFrom,
                    if (approvedSearchOptions.priceTo == 50000f) null else approvedSearchOptions.priceTo,
                    approvedSearchOptions.dateFrom?.timeInMillis,
                    approvedSearchOptions.dateTo?.timeInMillis,
                    if (approvedSearchOptions.categories.isEmpty()) null else approvedSearchOptions.categories,
                    approvedSearchOptions.isIndividual,
                    approvedSearchOptions.isGroup,
                    if (approvedSearchOptions.durationFrom == 0f) null else approvedSearchOptions.durationFrom,
                    if (approvedSearchOptions.durationTo == 72f) null else approvedSearchOptions.durationTo,
                    if (approvedSearchOptions.transport.isEmpty()) null else approvedSearchOptions.transport,
                    approvedSearchOptions.ageRestriction,
                    approvedSearchOptions.city?.cityId,
                    if (approvedSearchOptions.ratingFrom == 0f) null else approvedSearchOptions.ratingFrom,
                    if (approvedSearchOptions.ratingTo == 5f) null else approvedSearchOptions.ratingTo,
                )
            )


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
                    when {
                        response.body.contains("Connection") -> dataResultMutableLiveData.postValue(
                            UIStateAdvertList.ConnectionError
                        )

                        response.code == 404 -> dataResultMutableLiveData.postValue(UIStateAdvertList.Error("Произошла ошибка, попробуйте снова"))
                    }
                }
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