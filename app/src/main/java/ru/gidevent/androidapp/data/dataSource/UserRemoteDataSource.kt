package ru.gidevent.androidapp.data.dataSource

import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.SellerRequest
import ru.gidevent.androidapp.data.model.auth.response.EditProfile
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

    suspend fun userSignUpAsSeller(sellerRequest: SellerRequest) =
        networkHelper.safeApiCall { userApiService.signUpAsSeller(sellerRequest) }

    suspend fun becomeSeller() =
        networkHelper.safeApiCall { userApiService.becomeSeller() }

    suspend fun userRefreshToken(refreshBodyRequest: RefreshBodyRequest) =
        networkHelper.safeApiCall { userApiService.refreshToken(refreshBodyRequest) }

    suspend fun getUserById(credentials: String) =
        networkHelper.safeApiCall { userApiService.getUserById(credentials) }

    suspend fun getEditUserInfo(credentials: String) =
        networkHelper.safeApiCall { userApiService.getEditProfile(credentials) }

    suspend fun updateUser(credentials: String, editProfile: EditProfile) =
        networkHelper.safeApiCall { userApiService.updateUser(credentials, editProfile) }

    suspend fun userSignIn(accessToken: String, uuid: String) =
        networkHelper.safeApiCall { userApiService.loginFromVk(accessToken, uuid) }

}