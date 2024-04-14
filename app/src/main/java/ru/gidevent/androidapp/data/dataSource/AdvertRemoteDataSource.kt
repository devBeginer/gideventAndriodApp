package ru.gidevent.androidapp.data.dataSource

import okhttp3.MultipartBody
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory
import ru.gidevent.androidapp.data.model.advertisement.dto.NewFeedback
import ru.gidevent.androidapp.data.model.advertisement.request.EventTimeRequest
import ru.gidevent.androidapp.data.model.advertisement.request.NewAdvertisement
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPrice
import ru.gidevent.androidapp.data.model.advertisement.request.TicketPriceRequest
import ru.gidevent.androidapp.data.model.booking.BookingRequest
import ru.gidevent.androidapp.data.model.request.search.SearchOptions
import ru.gidevent.androidapp.data.service.AdvertisementApiService
import ru.gidevent.androidapp.network.NetworkHelper
import javax.inject.Inject

class AdvertRemoteDataSource @Inject constructor(
    private val advertApiService: AdvertisementApiService,
    private val networkHelper: NetworkHelper
) {

    suspend fun getTopAdvertList() =
        networkHelper.safeApiCall { advertApiService.getTopAdvertisement() }
    suspend fun getTopAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getTopAdvertisement(credentials) }
    suspend fun getSearchAdvertList(query: String) =
        networkHelper.safeApiCall { advertApiService.getAdvertByName(query) }
    suspend fun getSearchAdvertList(query: String, credentials: String) =
        networkHelper.safeApiCall { advertApiService.getAdvertByName(query, credentials) }
    suspend fun getAdvertList() =
        networkHelper.safeApiCall { advertApiService.getAllAdvertisement() }
    suspend fun getAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getAllAdvertisement(credentials) }
    suspend fun getFavouriteAdvertList(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getFavouriteAdvertisement(credentials) }
    suspend fun getPurchasesAdvertisement(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getPurchasesAdvertisement(credentials) }
    suspend fun getSuggestions(query: String) =
        networkHelper.safeApiCall { advertApiService.getSuggestionByName(query) }
    suspend fun getCitySuggestions(query: String) =
        networkHelper.safeApiCall { advertApiService.getCitySuggestion(query) }

    suspend fun getAdvertListByParams(credentials: String, searchOptions: SearchOptions) =
        networkHelper.safeApiCall { advertApiService.getAdvertByParams(searchOptions, credentials)}

    suspend fun getAdvertListByParams(searchOptions: SearchOptions) =
        networkHelper.safeApiCall { advertApiService.getAdvertByParams(searchOptions)}

    suspend fun getAdvertListById(credentials: String, id: Long) =
        networkHelper.safeApiCall { advertApiService.getAdvertisementById(credentials, id)}

    suspend fun getAdvertisementForEdit(credentials: String, id: Long) =
        networkHelper.safeApiCall { advertApiService.getAdvertisementForEdit(credentials, id)}

    suspend fun getAdvertListById(id: Long) =
        networkHelper.safeApiCall { advertApiService.getAdvertisementById(id)}

    suspend fun getAllTransport() =
        networkHelper.safeApiCall { advertApiService.getAllTransportations()}

    suspend fun getOptionsVariants() =
        networkHelper.safeApiCall { advertApiService.getOptionsVariants()}

    suspend fun getAllCategories() =
        networkHelper.safeApiCall { advertApiService.getAllCategory()}

    suspend fun getAllCustomerCategories() =
        networkHelper.safeApiCall { advertApiService.getAllCustomerCategory()}



    suspend fun postAdvertisement(credentials: String, newAdvertisement: NewAdvertisement) =
        networkHelper.safeApiCall { advertApiService.postAdvertisement(newAdvertisement, credentials)}

    suspend fun putAdvertisement(credentials: String, newAdvertisement: NewAdvertisement) =
        networkHelper.safeApiCall { advertApiService.putAdvertisement(newAdvertisement, credentials)}

    suspend fun deleteAdvertisement(credentials: String, advertisementId: Long) =
        networkHelper.safeApiCall { advertApiService.deleteAdvertisement(advertisementId, credentials)}

    suspend fun postEventTime(credentials: String, eventTimeRequest: EventTimeRequest) =
        networkHelper.safeApiCall { advertApiService.postEventTime(eventTimeRequest, credentials)}

    suspend fun getAllEventTime(id: Long) =
        networkHelper.safeApiCall { advertApiService.getAllEventTime(id)}

    suspend fun getAllTicketPrice(id: Long) =
        networkHelper.safeApiCall { advertApiService.getAllTicketPrice(id)}

    suspend fun putEventTime(credentials: String, eventTimeRequest: EventTimeRequest) =
        networkHelper.safeApiCall { advertApiService.putEventTime(eventTimeRequest, credentials)}

    suspend fun deleteEventTime(credentials: String, eventTimeId: Long) =
        networkHelper.safeApiCall { advertApiService.deleteEventTime(eventTimeId, credentials)}

    suspend fun postTicketPrice(credentials: String, ticketPrice: TicketPriceRequest) =
        networkHelper.safeApiCall { advertApiService.postTicketPrice(ticketPrice, credentials)}

    suspend fun putTicketPrice(credentials: String, ticketPrice: TicketPriceRequest) =
        networkHelper.safeApiCall { advertApiService.putTicketPrice(ticketPrice, credentials)}

    suspend fun deleteTicketPrice(credentials: String, ticketPriceId: Long) =
        networkHelper.safeApiCall { advertApiService.deleteTicketPrice(ticketPriceId, credentials)}

    suspend fun postCategory(credentials: String, category: Category) =
        networkHelper.safeApiCall { advertApiService.postCategory(category, credentials)}

    suspend fun putCategory(credentials: String, category: Category) =
        networkHelper.safeApiCall { advertApiService.putCategory(category, credentials)}

    suspend fun postTransportationVariant(credentials: String, transportationVariant: TransportationVariant) =
        networkHelper.safeApiCall { advertApiService.postTransportationVariant(transportationVariant, credentials)}

    suspend fun putTransportationVariant(credentials: String, transportationVariant: TransportationVariant) =
        networkHelper.safeApiCall { advertApiService.putTransportationVariant(transportationVariant, credentials)}

    suspend fun postCustomerCategory(credentials: String, category: CustomerCategory) =
        networkHelper.safeApiCall { advertApiService.postCustomerCategory(category, credentials)}

    suspend fun putCustomerCategory(credentials: String, category: CustomerCategory) =
        networkHelper.safeApiCall { advertApiService.putCustomerCategory(category, credentials)}

    suspend fun postPhoto(credentials: String, image: MultipartBody.Part) =
        networkHelper.safeApiCall { advertApiService.insertPhoto(image, credentials)}

    suspend fun delPhoto(credentials: String, fileUUID: String) =
        networkHelper.safeApiCall { advertApiService.deletePhoto(fileUUID, credentials)}



    suspend fun postFeedback(credentials: String, newFeedback: NewFeedback) =
        networkHelper.safeApiCall { advertApiService.postFeedback(newFeedback, credentials)}

    suspend fun putFeedback(credentials: String, newFeedback: NewFeedback) =
        networkHelper.safeApiCall { advertApiService.putFeedback(newFeedback, credentials)}

    suspend fun getFeedback(credentials: String, advertId: Long) =
        networkHelper.safeApiCall { advertApiService.getFeedback(advertId, credentials)}


    suspend fun postFavourite(credentials: String, advertId: Long) =
        networkHelper.safeApiCall { advertApiService.postFavourite(advertId, credentials)}


    suspend fun getBookingParams(credentials: String, advertId: Long, dateParam: Long) =
        networkHelper.safeApiCall { advertApiService.getBookingParams(advertId, dateParam, credentials)}


    suspend fun postBooking(credentials: String, bookingRequest: BookingRequest) =
        networkHelper.safeApiCall { advertApiService.postBooking(bookingRequest, credentials)}


    suspend fun postBookingConfirmation(credentials: String, bookingId: Long) =
        networkHelper.safeApiCall { advertApiService.postBookingConfirmation(bookingId, credentials)}


    suspend fun getBookingInfo(credentials: String, bookingId: Long) =
        networkHelper.safeApiCall { advertApiService.getBookingInfo(bookingId, credentials)}


    suspend fun getSellerBooking(credentials: String, advertId: Long, date: Long?) =
        networkHelper.safeApiCall { advertApiService.getSellerBooking(advertId, date, credentials)}
    suspend fun getAdvertChips(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getAdvertChips(credentials)}


    suspend fun getSellerAdvert(credentials: String) =
        networkHelper.safeApiCall { advertApiService.getSellerAdvert(credentials)}

}