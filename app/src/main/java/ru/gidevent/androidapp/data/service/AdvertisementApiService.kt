package ru.gidevent.androidapp.data.service

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.RestAPI.model.response.Suggestions
import ru.gidevent.RestAPI.response.TopsResponse
import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.data.model.advertisement.request.EventTimeRequest
import ru.gidevent.androidapp.data.model.advertisement.request.NewAdvertisement
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPrice
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPriceRequest
import ru.gidevent.androidapp.data.model.advertisement.response.AdvertisementExpanded
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.advertisement.response.EventTimeResponse
import ru.gidevent.androidapp.data.model.advertisement.response.ResponsePoster
import ru.gidevent.androidapp.data.model.advertisement.response.TicketPriceResponse
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

    @GET("auth/customerCategory/")
    suspend fun getAllCustomerCategory(): Response<List<CustomerCategory>>

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



    @POST("advertisement/")
    suspend fun postAdvertisement(
        @Body newAdvertisement: NewAdvertisement,
        @Header("Authorization") token: String
    ): Response<Advertisement>

    @POST("eventTime/")
    suspend fun postEventTime(
        @Body eventTimeRequest: EventTimeRequest,
        @Header("Authorization") token: String
    ): Response<EventTimeResponse>

    @POST("ticketPrice/")
    suspend fun postTicketPrice(
        @Body ticketPrice: TicketPriceRequest,
        @Header("Authorization") token: String
    ): Response<TicketPriceResponse>

    @POST("category/")
    suspend fun postCategory(
        @Body category: Category,
        @Header("Authorization") token: String
    ): Response<Category>

    @POST("transportationVariant/")
    suspend fun postTransportationVariant(
        @Body transportationVariant: TransportationVariant,
        @Header("Authorization") token: String
    ): Response<TransportationVariant>

    @POST("customerCategory/")
    suspend fun postCustomerCategory(
        @Body category: CustomerCategory,
        @Header("Authorization") token: String
    ): Response<CustomerCategory>



    @PUT("advertisement/")
    suspend fun putAdvertisement(
        @Body newAdvertisement: NewAdvertisement,
        @Header("Authorization") token: String
    ): Response<Advertisement>

    @PUT("eventTime/")
    suspend fun putEventTime(
        @Body eventTimeRequest: EventTimeRequest,
        @Header("Authorization") token: String
    ): Response<EventTimeResponse>

    @PUT("ticketPrice/")
    suspend fun putTicketPrice(
        @Body ticketPrice: TicketPriceRequest,
        @Header("Authorization") token: String
    ): Response<TicketPriceResponse>

    @PUT("category/")
    suspend fun putCategory(
        @Body category: Category,
        @Header("Authorization") token: String
    ): Response<Category>

    @PUT("transportationVariant/")
    suspend fun putTransportationVariant(
        @Body category: TransportationVariant,
        @Header("Authorization") token: String
    ): Response<TransportationVariant>

    @PUT("customerCategory/")
    suspend fun putCustomerCategory(
        @Body category: CustomerCategory,
        @Header("Authorization") token: String
    ): Response<CustomerCategory>



    @GET("auth/advertisement/eventTime/{id}")
    suspend fun getAllEventTime(@Path("id") id: Long): Response<List<EventTimeResponse>>

    @GET("auth/advertisement/ticketPrice/{id}")
    suspend fun getAllTicketPrice(@Path("id") id: Long): Response<List<TicketPriceResponse>>


    @Multipart
    @POST("photo/")
    suspend fun insertPhoto(@Part image: MultipartBody.Part,
                            @Header("Authorization") token: String): Response<ResponsePoster>

    @DELETE("photo/")
    suspend fun deletePhoto(@Path("fileUUID") fileUUID: String,
                            @Header("Authorization") token: String): Response<ResponsePoster>

}