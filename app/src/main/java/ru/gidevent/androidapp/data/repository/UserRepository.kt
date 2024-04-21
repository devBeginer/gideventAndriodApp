package ru.gidevent.androidapp.data.repository

import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.dataSource.UserRemoteDataSource
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.SellerRequest
import ru.gidevent.androidapp.data.model.auth.response.EditProfile
import ru.gidevent.androidapp.data.model.auth.response.ProfileResponse
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

    suspend fun userSignUpAsSeller(sellerRequest: SellerRequest) =
        remoteDataSource.userSignUpAsSeller(sellerRequest)

    suspend fun userRefreshToken(refreshBodyRequest: RefreshBodyRequest) =
        remoteDataSource.userRefreshToken(refreshBodyRequest)

    suspend fun getUserById(): ApiResult<UserDetailsResponse?> {
        val token = localDataSource.getAccessTokenFromSP()
        return remoteDataSource.getUserById(token)
    }

    suspend fun getEditUserInfo(): ApiResult<EditProfile?> {
        val token = localDataSource.getAccessTokenFromSP()
        return remoteDataSource.getEditUserInfo(token)
    }

    suspend fun updateUser(editProfile: EditProfile): ApiResult<ProfileResponse?> {
        val token = localDataSource.getAccessTokenFromSP()
        return remoteDataSource.updateUser(token, editProfile)
    }

    fun saveAccessTokenToSP(token: String) {
        localDataSource.saveAccessTokenToSP("Bearer $token")
    }

    fun saveRefreshTokenToSP(refreshToken: String) {
        localDataSource.saveRefreshTokenToSP(refreshToken)
    }

    fun saveCredentialsToSP(loginBodyResponse: LoginBodyResponse) {
        localDataSource.saveAccessTokenToSP("Bearer ${loginBodyResponse.accessToken}")
        localDataSource.saveRefreshTokenToSP(loginBodyResponse.refreshToken)
    }

    fun isAuthorised(): Boolean{
        return localDataSource.getAccessTokenFromSP()!=""
    }



    fun logout() {
        localDataSource.resetTokensInSP()
    }
}