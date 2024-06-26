package ru.gidevent.androidapp.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.RestAPI.auth.RegisterBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.LoginBodyRequest
import ru.gidevent.androidapp.data.model.auth.request.SellerRequest
import ru.gidevent.androidapp.data.model.auth.response.EditProfile
import ru.gidevent.androidapp.data.model.auth.response.ProfileResponse
import ru.gidevent.androidapp.data.model.auth.response.RegisterBodyResponse
import ru.gidevent.androidapp.data.model.auth.response.SellerResponse
import ru.gidevent.androidapp.data.model.auth.response.UserDetailsResponse

interface UserApiService {

    @POST("auth/login")
    suspend fun signIn(@Body loginBodyRequest: LoginBodyRequest): Response<LoginBodyResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body registerBodyRequest: RegisterBodyRequest): Response<RegisterBodyResponse>

    @POST("auth/seller/")
    suspend fun signUpAsSeller(@Body sellerRequest: SellerRequest): Response<SellerResponse>

    @POST("seller/")
    suspend fun becomeSeller(): Response<SellerResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshBodyRequest: RefreshBodyRequest): Response<LoginBodyResponse>

    @PUT("profile")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body editProfile: EditProfile
    ): Response<ProfileResponse>

    @GET("profile")
    suspend fun getUserById(@Header("Authorization") token: String): Response<UserDetailsResponse?>

    @GET("editProfile")
    suspend fun getEditProfile(@Header("Authorization") token: String): Response<EditProfile?>

    @POST("auth/vkAuth")
    suspend fun loginFromVk(@Query("accessToken") accessToken: String, @Query("uuid") uuid: String): Response<LoginBodyResponse>
}