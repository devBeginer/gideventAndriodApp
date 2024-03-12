package ru.gidevent.androidapp.data.dataSource

import ru.gidevent.androidapp.data.model.request.search.SearchOptions
import ru.gidevent.androidapp.data.service.AdvertisementApiService
import ru.gidevent.androidapp.network.NetworkHelper
import javax.inject.Inject

class AdvertRemoteDataSource @Inject constructor(
    private val advertApiService: AdvertisementApiService,
    private val networkHelper: NetworkHelper
) {

    suspend fun getTopAdvertList() =
        networkHelper.safeApiCall { advertApiService.getTopAdvertisement() }
    suspend fun getTopAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getTopAdvertisement(credentials) }
    suspend fun getSearchAdvertList(query: String) =
        networkHelper.safeApiCall { advertApiService.getAdvertByName(query) }
    suspend fun getSearchAdvertList(query: String, credentials: String) =
        networkHelper.safeApiCall { advertApiService.getAdvertByName(query, credentials) }
    suspend fun getAdvertList() =
        networkHelper.safeApiCall { advertApiService.getAllAdvertisement() }
    suspend fun getAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getAllAdvertisement(credentials) }
    suspend fun getFavouriteAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getFavouriteAdvertisement(credentials) }
    suspend fun getSuggestions(query: String) =
        networkHelper.safeApiCall { advertApiService.getSuggestionByName(query) }
    suspend fun getCitySuggestions(query: String) =
        networkHelper.safeApiCall { advertApiService.getCitySuggestion(query) }

    suspend fun getAdvertListByParams(credentials: String, searchOptions: SearchOptions) =
        networkHelper.safeApiCall { advertApiService.getAdvertByParams(searchOptions, credentials)}

    suspend fun getAdvertListByParams(searchOptions: SearchOptions) =
        networkHelper.safeApiCall { advertApiService.getAdvertByParams(searchOptions)}

    suspend fun getAllTransport() =
        networkHelper.safeApiCall { advertApiService.getAllTransportations()}

    suspend fun getOptionsVariants() =
        networkHelper.safeApiCall { advertApiService.getOptionsVariants()}

    suspend fun getAllCategories() =
        networkHelper.safeApiCall { advertApiService.getAllCategory()}

}