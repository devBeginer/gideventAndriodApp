package ru.gidevent.androidapp.data.dataSource

import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.service.UserApiService
import ru.gidevent.androidapp.network.NetworkHelper
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userApiService: UserApiService,
    private val networkHelper: NetworkHelper
) {

    suspend fun userSignIn(loginBodyRequest: LoginBodyRequest) =
        networkHelper.safeApiCall { userApiService.signIn(loginBodyRequest) }

    suspend fun userSignUp(registerBodyRequest: RegisterBodyRequest) =
        networkHelper.safeApiCall { userApiService.signUp(registerBodyRequest) }

    suspend fun userRefreshToken(refreshBodyRequest: RefreshBodyRequest) =
        networkHelper.safeApiCall { userApiService.refreshToken(refreshBodyRequest) }

    suspend fun getUserById(credentials: String) =
        networkHelper.safeApiCall { userApiService.getUserById(credentials) }

}