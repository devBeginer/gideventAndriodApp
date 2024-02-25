package ru.gidevent.androidapp.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.gidevent.RestAPI.auth.LoginBodyResponse
import ru.gidevent.RestAPI.auth.RefreshBodyRequest
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.service.RefreshApiService
import javax.inject.Inject

class JwtAuthenticator @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val refreshApiService: RefreshApiService,
    private val networkHelper: NetworkHelper
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val token = getAccessToken()
        if (response.request.header("Authorization") == null) {
            // NOTE: this can be improved by checking the expiration date locally instead of
            // sending a request to the API which will result in a 401.

            // Adding the access token to the request.
            return response.request.newBuilder()
                .header("Authorization", token)
                .build()
        }

        if (response.code == 401 && token != "") {
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
                            return@runBlocking response.request.newBuilder()
                                .header("Authorization", "Bearer ${newCredentials.accessToken}")
                                .build()
                        } else {

                            return@runBlocking null
                        }
                    } catch (e: Exception) {
                        // The refresh process failed. Give up on retrying the request.

                        return@runBlocking null
                    }
                }
            }
        }

        // Use the authenticated original request.
        return response.request
    }

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
}

/*
class TokenAuthenticator(
    private val tokenManager: TokenManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request {
        synchronized(this) {
            val sessionData = if (isRefreshNeeded(response)) {
                runBlocking { getUpdatedSessionData() }
            } else {
                getExistingSessionData()
            }

            return response.request.newBuilder()
                .header(HeaderKeys.SESSION_ID, sessionData.sessionId)
                .header(HeaderKeys.REFRESH_ID, sessionData.refreshId)
                .build()
        }
    }

    private fun isRefreshNeeded(response: Response): Boolean {
        val oldSessionId = response.request.header(HeaderKeys.SESSION_ID)
        val oldRefreshId = response.request.header(HeaderKeys.REFRESH_ID)

        val updatedSessionId = tokenManager.getSessionId()
        val updatedRefreshId = tokenManager.getRefreshId()

        return (oldSessionId == updatedSessionId && oldRefreshId == updatedRefreshId)
    }

    private fun getExistingSessionData(): ApiResponse.SessionData {
        val updatedSessionId = tokenManager.getSessionId()
        val updatedRefreshId = tokenManager.getRefreshId()
        return ApiResponse.SessionData(
            sessionId = updatedSessionId,
            refreshId = updatedRefreshId
        )
    }

    private suspend fun getUpdatedSessionData(): ApiResponse.SessionData {
        val refreshTokenRequest =
            ApiResponse.RefreshSessionRequest(tokenManager.getRefreshId())
        return when (val result =
            getResult { userApiService().refreshSession(refreshTokenRequest) }) {
            is ApiResult.Success -> {
                val sessionData = result.data.data
                tokenManager.saveSessionId(sessionData.sessionId)
                tokenManager.saveRefreshId(sessionData.refreshId)
                delay(50)
                sessionData
            }
            is ApiResult.Error -> {
                MySdk.instance().mySdkListeners?.onSessionExpired()
                return ApiResponse.SessionData()
            }
        }
    }

    private class CustomNetworkStateChecker : NetworkStateChecker {
        override fun isNetworkAvailable() = true
    }

    private fun userApiService(): UserApiService {
        val retrofit = RetrofitHelper.provideRetrofit(
            RetrofitHelper.provideOkHttpClient(CustomNetworkStateChecker(), tokenManager)
        )
        return retrofit.create(UserApiService::class.java)
    }
}*/
