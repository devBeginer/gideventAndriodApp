package ru.gidevent.androidapp.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.service.RefreshApiService
import javax.inject.Inject

class JwtAuthInterceptor @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val refreshApiService: RefreshApiService,
    private val networkHelper: NetworkHelper
) : Interceptor {


    private fun getAccessToken(): String {
        return userLocalDataSource.getAccessTokenFromSP()
    }

    private fun updateCredentials(refreshToken: String, accessToken: String) {

        userLocalDataSource.saveAccessTokenToSP("Bearer $accessToken")
        userLocalDataSource.saveRefreshTokenToSP(refreshToken)
    }

    private suspend fun refreshCredentials(): LoginBodyResponse? {

        val result = networkHelper.safeApiCall {
            refreshApiService.refreshToken(
                RefreshBodyRequest(userLocalDataSource.getRefreshTokenFromSP())
            )
        }


        return if (result is ApiResult.Success) result.data else null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // try the request
        val response = chain.proceed(request)
        val token = getAccessToken()


        if (response.code == 403 && token != "") {
            // The access token is expired. Refresh the credentials.
            synchronized(this) {
                // Make sure only one coroutine refreshes the token at a time.
                return runBlocking {
                    try {
                        val newCredentials = refreshCredentials()

                        // Update the access token in your storage.
                        if (newCredentials != null) {
                            updateCredentials(
                                newCredentials.refreshToken,
                                newCredentials.accessToken
                            )
                            val newRequest = request.newBuilder()
                                .header("Authorization", "Bearer ${newCredentials.accessToken}")
                                .build()
                            return@runBlocking chain.proceed(newRequest)
                        } else {

                            return@runBlocking response
                        }
                    } catch (e: Exception) {
                        // The refresh process failed. Give up on retrying the request.

                        return@runBlocking response
                    }
                }
            }
        }

        // otherwise just pass the original response on
        return response
    }

}