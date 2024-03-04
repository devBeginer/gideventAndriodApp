package ru.gidevent.androidapp.data.dataSource

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
    suspend fun getAdvertList() =
        networkHelper.safeApiCall { advertApiService.getAllAdvertisement() }
    suspend fun getAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getAllAdvertisement(credentials) }
    suspend fun getFavouriteAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getFavouriteAdvertisement(credentials) }

}