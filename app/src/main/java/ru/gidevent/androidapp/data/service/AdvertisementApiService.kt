package ru.gidevent.androidapp.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.RestAPI.model.response.Suggestions
import ru.gidevent.RestAPI.response.TopsResponse
import ru.gidevent.androidapp.data.model.advertisement.response.AdvertisementExpanded
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.request.search.SearchOptions
import ru.gidevent.androidapp.data.model.search.OptionsVariants

interface AdvertisementApiService {

    @GET("auth/advertisement/")
    suspend fun getAllAdvertisement(): Response<List<Advertisement>>

    @GET("auth/advertisement/top")
    suspend fun getTopAdvertisement(): Response<TopsResponse?>

    @GET("search/query")
    suspend fun getAdvertByName(@Query(value="query", encoded=true) query: String, @Header("Authorization") token: String): Response<List<Advertisement>>

    @GET("auth/search/query")
    suspend fun getAdvertByName(@Query(value="query", encoded=true) query: String): Response<List<Advertisement>>

    @GET("auth/search/suggestion")
    suspend fun getSuggestionByName(@Query(value="query", encoded=true) query: String): Response<Suggestions?>

    @GET("advertisement/")
    suspend fun getAllAdvertisement(@Header("Authorization") token: String): Response<List<Advertisement>>

    @GET("advertisement/top")
    suspend fun getTopAdvertisement(@Header("Authorization") token: String): Response<TopsResponse?>

    @GET("advertisement/favourite")
    suspend fun getFavouriteAdvertisement(@Header("Authorization") token: String): Response<List<Advertisement>>

    @GET("auth/advertisement/{id}")
    suspend fun getAdvertisementById(@Path("id") id: Long): Response<AdvertisementExpanded>

    @GET("advertisement/{id}")
    suspend fun getAdvertisementById(@Header("Authorization") token: String, @Path("id") id: Long): Response<AdvertisementExpanded>

    @GET("auth/category/")
    suspend fun getAllCategory(): Response<List<Category>>

    @GET("auth/category/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): Response<Category>

    @GET("auth/transportationVariant/")
    suspend fun getAllTransportations(): Response<List<TransportationVariant>>

    @GET("auth/search/optionsVariants")
    suspend fun getOptionsVariants(): Response<OptionsVariants?>

    @GET("auth/transportationVariant/{id}")
    suspend fun getTransportationById(@Path("id") id: Long): Response<TransportationVariant>

    @POST("search/params")
    suspend fun getAdvertByParams(@Body searchOptions: SearchOptions, @Header("Authorization") token: String): Response<List<Advertisement>>

    @POST("auth/search/params")
    suspend fun getAdvertByParams(@Body searchOptions: SearchOptions): Response<List<Advertisement>>

    @GET("auth/search/suggestion/city")
    suspend fun getCitySuggestion(@Query(value="query", encoded=true) name: String): Response<List<CitySuggestion>>


}