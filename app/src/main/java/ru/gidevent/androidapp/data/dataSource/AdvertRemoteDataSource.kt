package ru.gidevent.androidapp.data.dataSource

import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.service.AdvertisementApiService
import ru.gidevent.androidapp.data.service.UserApiService
import ru.gidevent.androidapp.network.NetworkHelper
import javax.inject.Inject

class AdvertRemoteDataSource @Inject constructor(
    private val advertApiService: AdvertisementApiService,
    private val networkHelper: NetworkHelper
) {

    suspend fun getTopAdvertList() =
        networkHelper.safeApiCall { advertApiService.getTopAdvertisement() }

}