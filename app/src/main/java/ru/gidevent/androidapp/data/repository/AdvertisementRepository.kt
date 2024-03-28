package ru.gidevent.androidapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.RestAPI.model.response.Suggestions
import ru.gidevent.RestAPI.response.TopsResponse
import ru.gidevent.androidapp.data.dataSource.AdvertLocalDataSource
import ru.gidevent.androidapp.data.dataSource.AdvertRemoteDataSource
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.data.model.advertisement.dto.NewFeedback
import ru.gidevent.androidapp.data.model.advertisement.request.EventTimeRequest
import ru.gidevent.androidapp.data.model.advertisement.request.NewAdvertisement
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPrice
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPriceRequest
import ru.gidevent.androidapp.data.model.advertisement.response.AdvertisementExpanded
import ru.gidevent.androidapp.data.model.advertisement.response.Advertisement
import ru.gidevent.androidapp.data.model.advertisement.response.EventTimeResponse
import ru.gidevent.androidapp.data.model.advertisement.response.NewFeedbackResponse
import ru.gidevent.androidapp.data.model.advertisement.response.ResponsePoster
import ru.gidevent.androidapp.data.model.advertisement.response.TicketPriceResponse
import ru.gidevent.androidapp.data.model.request.search.SearchOptions
import ru.gidevent.androidapp.data.model.search.OptionsVariants
import ru.gidevent.androidapp.network.ApiResult
import ru.gidevent.androidapp.utils.Utils
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AdvertisementRepository @Inject constructor(
    private val advertRemoteDataSource: AdvertRemoteDataSource,
    private val advertLocalDataSource: AdvertLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    @ApplicationContext private val context: Context
) {



    suspend fun getTopAdvertisement(): ApiResult<TopsResponse?> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getTopAdvertList(token)
        }else{
            advertRemoteDataSource.getTopAdvertList()
        }
    }

    suspend fun getSearchAdvertisement(query: String): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getSearchAdvertList(query, token)
        }else{
            advertRemoteDataSource.getSearchAdvertList(query)
        }
    }

    suspend fun getAllAdvertisement(): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getAdvertList(token)
        }else{
            advertRemoteDataSource.getAdvertList()
        }
    }

    suspend fun getFavouriteAdvertisement(): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.getFavouriteAdvertList(token)

    }

    suspend fun getSearchSuggestions(query: String): ApiResult<Suggestions?> {
        return advertRemoteDataSource.getSuggestions(query)
    }

    suspend fun getCitySuggestions(query: String): ApiResult<List<CitySuggestion>> {
        return advertRemoteDataSource.getCitySuggestions(query)
    }



    suspend fun getAdvertisementByParams(searchOptions: SearchOptions): ApiResult<List<Advertisement>> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getAdvertListByParams(token, searchOptions)
        }else{
            advertRemoteDataSource.getAdvertListByParams(searchOptions)
        }
    }



    suspend fun getAdvertisementById(id: Long): ApiResult<AdvertisementExpanded> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return if(token!=""){
            advertRemoteDataSource.getAdvertListById(token, id)
        }else{
            advertRemoteDataSource.getAdvertListById(id)
        }
    }



    suspend fun getAllTransport(): ApiResult<List<TransportationVariant>> {
        return advertRemoteDataSource.getAllTransport()
    }

    suspend fun getOptionsVariants(): ApiResult<OptionsVariants?> {
        return advertRemoteDataSource.getOptionsVariants()
    }



    suspend fun getAllCategory(): ApiResult<List<Category>> {
        return advertRemoteDataSource.getAllCategories()
    }



    suspend fun getAllCustomerCategory(): ApiResult<List<CustomerCategory>> {
        return advertRemoteDataSource.getAllCustomerCategories()
    }





    suspend fun postAdvertisement(newAdvertisement: NewAdvertisement): ApiResult<Advertisement> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.postAdvertisement(token, newAdvertisement)
    }

    suspend fun putAdvertisement(newAdvertisement: NewAdvertisement): ApiResult<Advertisement> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.putAdvertisement(token, newAdvertisement)
    }

    suspend fun postEventTime(eventTime: EventTimeRequest): ApiResult<EventTimeResponse> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.postEventTime(token, eventTime)
    }

    suspend fun getAllEventTime(id: Long): ApiResult<List<EventTimeResponse>> {
        return advertRemoteDataSource.getAllEventTime(id)
    }
    suspend fun putEventTime(eventTime: EventTimeRequest): ApiResult<EventTimeResponse> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.putEventTime(token, eventTime)
    }

    suspend fun getAllTicketPrice(id: Long): ApiResult<List<TicketPriceResponse>> {
        return advertRemoteDataSource.getAllTicketPrice(id)
    }


    suspend fun postTicketPrice(ticketPrice: TicketPriceRequest): ApiResult<TicketPriceResponse> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.postTicketPrice(token, ticketPrice)
    }

    suspend fun putTicketPrice(ticketPrice: TicketPriceRequest): ApiResult<TicketPriceResponse> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.putTicketPrice(token, ticketPrice)
    }

    suspend fun postCategory(category: Category): ApiResult<Category> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.postCategory(token, category)
    }

    suspend fun putCategory(category: Category): ApiResult<Category> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.putCategory(token, category)
    }

    suspend fun postTransportationVariant(transportationVariant: TransportationVariant): ApiResult<TransportationVariant> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.postTransportationVariant(token, transportationVariant)
    }

    suspend fun putTransportationVariant(transportationVariant: TransportationVariant): ApiResult<TransportationVariant> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.putTransportationVariant(token, transportationVariant)
    }

    suspend fun postCustomerCategory(category: CustomerCategory): ApiResult<CustomerCategory> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.postCustomerCategory(token, category)
    }

    suspend fun putCustomerCategory(category: CustomerCategory): ApiResult<CustomerCategory> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.putCustomerCategory(token, category)
    }

    suspend fun insertPoster(fileUri: Uri): ApiResult<ResponsePoster?> {
        val file = File(context.cacheDir, "tmp")
        val byteArray: ByteArray?
        withContext(Dispatchers.IO) {
            file.createNewFile()
            val inputStream = context.contentResolver.openInputStream(fileUri).use { input ->
                byteArray = input?.readBytes()
            }
            FileOutputStream(file).buffered().use { output ->
                //BitmapFactory.decodeStream(input, null, BitmapFactory.Options())
                Utils.decodeSampledBitmap(byteArray, 1000, 1000)
                    ?.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        output
                    )
            }
        }

        val requestBody = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val token = userLocalDataSource.getAccessTokenFromSP()

        return advertRemoteDataSource.postPhoto(token, MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = requestBody
        ))
    }

    /*suspend fun getPoster(fileUUID: String): String? {
        val body = filmApiInterface.getPosterById(fileUUID).body()


        return if (body != null) {
            val file = File(context.filesDir, fileUUID)
            if (!file.exists()) {
                file.createNewFile()
                val input = body.byteStream()

                withContext(Dispatchers.IO) {
                    FileOutputStream(file).buffered().use { output ->
                        BitmapFactory.decodeStream(input, null, BitmapFactory.Options())
                            ?.compress(
                                Bitmap.CompressFormat.JPEG,
                                100,
                                output
                            )
                    }
                }
            }
            file.toURI().toString()
        } else {
            null
        }
    }*/

    suspend fun postFeedback(newFeedback: NewFeedback): ApiResult<NewFeedbackResponse> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.postFeedback(token, newFeedback)
    }

    suspend fun putFeedback(newFeedback: NewFeedback): ApiResult<NewFeedbackResponse> {
        val token = userLocalDataSource.getAccessTokenFromSP()
        return advertRemoteDataSource.putFeedback(token, newFeedback)
    }

    suspend fun postFavourite(advertId: Long): ApiResult<Advertisement> {
        val token = userLocalDataSource.getAccessTokenFromSP()

        return advertRemoteDataSource.postFavourite(token, advertId)
    }
}