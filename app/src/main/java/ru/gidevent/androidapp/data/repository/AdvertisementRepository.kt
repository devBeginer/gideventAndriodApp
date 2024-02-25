package ru.gidevent.androidapp.data.repository

import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.RestAPI.response.TopsResponse
import ru.gidevent.androidapp.data.dataSource.AdvertLocalDataSource
import ru.gidevent.androidapp.data.dataSource.AdvertRemoteDataSource
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.dataSource.UserRemoteDataSource
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.network.ApiResult
import javax.inject.Inject

class AdvertisementRepository @Inject constructor(
    private val advertRemoteDataSource: AdvertRemoteDataSource,
    private val advertLocalDataSource: AdvertLocalDataSource
) {



    suspend fun getTopAdvertisement(): ApiResult<TopsResponse?> {
        return advertRemoteDataSource.getTopAdvertList()
    }


}