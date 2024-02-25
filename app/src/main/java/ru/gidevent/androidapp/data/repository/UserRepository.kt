package ru.gidevent.androidapp.data.repository

import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.dataSource.UserRemoteDataSource
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse
import ru.gidevent.androidapp.network.ApiResult
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
) {

    suspend fun userSignIn(loginBodyRequest: LoginBodyRequest) =
        remoteDataSource.userSignIn(loginBodyRequest)

    suspend fun userSignUp(registerBodyRequest: RegisterBodyRequest) =
        remoteDataSource.userSignUp(registerBodyRequest)

    suspend fun userRefreshToken(refreshBodyRequest: RefreshBodyRequest) =
        remoteDataSource.userRefreshToken(refreshBodyRequest)

    suspend fun getUserById(): ApiResult<UserDetailsResponse?> {
        val token = localDataSource.getAccessTokenFromSP()
        return remoteDataSource.getUserById(token)
    }

    fun saveAccessTokenToSP(token: String) {
        return localDataSource.saveAccessTokenToSP("Bearer $token")
    }


}