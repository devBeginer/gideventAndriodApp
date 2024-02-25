package ru.gidevent.androidapp.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.auth.RefreshBodyRequest

interface RefreshApiService {
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshBodyRequest: RefreshBodyRequest): Response<LoginBodyResponse>


}