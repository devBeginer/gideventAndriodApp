package ru.gidevent.androidapp.data.repository

import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.RestAPI.model.response.Suggestions
import ru.gidevent.RestAPI.response.TopsResponse
import ru.gidevent.androidapp.data.dataSource.AdvertLocalDataSource
import ru.gidevent.androidapp.data.dataSource.AdvertRemoteDataSource
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.dataSource.UserRemoteDataSource
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.data.model.request.search.SearchOptions
import ru.gidevent.androidapp.data.model.search.OptionsVariants
import ru.gidevent.androidapp.network.ApiResult
import javax.inject.Inject

class AdvertisementRepository @Inject constructor(
    private val advertRemoteDataSource: AdvertRemoteDataSource,
    private val advertLocalDataSource: AdvertLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource
) {



    suspend fun getTopAdvertisement(): ApiResult<TopsResponse?> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getTopAdvertList(token)
        }else{
            advertRemoteDataSource.getTopAdvertList()
        }
    }

    suspend fun getSearchAdvertisement(query: String): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getSearchAdvertList(query, token)
        }else{
            advertRemoteDataSource.getSearchAdvertList(query)
        }
    }

    suspend fun getAllAdvertisement(): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getAdvertList(token)
        }else{
            advertRemoteDataSource.getAdvertList()
        }
    }

    suspend fun getFavouriteAdvertisement(): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.getFavouriteAdvertList(token)

    }

    suspend fun getSearchSuggestions(query: String): ApiResult<Suggestions?> {
        return advertRemoteDataSource.getSuggestions(query)
    }

    suspend fun getCitySuggestions(query: String): ApiResult<List<CitySuggestion>> {
        return advertRemoteDataSource.getCitySuggestions(query)
    }



    suspend fun getAdvertisementByParams(searchOptions: SearchOptions): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getAdvertListByParams(token, searchOptions)
        }else{
            advertRemoteDataSource.getAdvertListByParams(searchOptions)
        }
    }



    suspend fun getAllTransport(): ApiResult<List<TransportationVariant>> {
        return advertRemoteDataSource.getAllTransport()
    }

    suspend fun getOptionsVariants(): ApiResult<OptionsVariants?> {
        return advertRemoteDataSource.getOptionsVariants()
    }



    suspend fun getAllCategory(): ApiResult<List<Category>> {
        return advertRemoteDataSource.getAllCategories()
    }
}