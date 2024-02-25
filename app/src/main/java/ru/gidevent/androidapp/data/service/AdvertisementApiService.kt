package ru.gidevent.androidapp.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.response.TopsResponse
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.response.RegisterBodyResponse
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse

interface AdvertisementApiService {

    @GET("auth/advertisement/")
    suspend fun getAllAdvertisement(): Response<List<Advertisement>>

    @GET("auth/advertisement/top")
    suspend fun getTopAdvertisement(): Response<TopsResponse?>

    @GET("auth/advertisement/{id}")
    suspend fun getAdvertisementById(@Path("id") id: Long): Response<Advertisement>

    @GET("auth/category/")
    suspend fun getAllCategory(): Response<List<Category>>

    @GET("auth/category/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): Response<Category>


}