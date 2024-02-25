package ru.gidevent.androidapp.di

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.gidevent.androidapp.data.dataSource.UserLocalDataSource
import ru.gidevent.androidapp.data.service.RefreshApiService
import ru.gidevent.androidapp.network.JwtAuthInterceptor
import ru.gidevent.androidapp.network.JwtAuthenticator
import ru.gidevent.androidapp.network.NetworkHelper
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideBaseUrl() = "http://10.0.2.2:8080/api/"

    @AuthOkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(jwtAuthenticator: JwtAuthenticator, interceptor: JwtAuthInterceptor) = /*OkHttpClient.Builder()*/
        getUnsafeOkHttpClientBuilder()
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .authenticator(jwtAuthenticator)
            .addInterceptor(interceptor)
            .build()


    @PublicOkHttpClient
    @Provides
    @Singleton
    fun providePublicOkHttpClient() = /*OkHttpClient.Builder()*/
        getUnsafeOkHttpClientBuilder()
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    private fun getUnsafeOkHttpClientBuilder(): OkHttpClient.Builder {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })

            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()
    }


    @AuthRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        BASE_URL: String,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            //.addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()


    @PublicRetrofit
    @Provides
    @Singleton
    fun providePublicRetrofit(
        @PublicOkHttpClient okHttpClient: OkHttpClient,
        BASE_URL: String,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            //.addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()


    @Provides
    @Singleton
    fun provideNetworkHelper(application: Application): NetworkHelper = NetworkHelper(application)

    @Provides
    @Singleton
    fun provideJwtAuthenticator(
        userLocalDataSource: UserLocalDataSource,
        refreshApiService: RefreshApiService,
        networkHelper: NetworkHelper
    ): JwtAuthenticator = JwtAuthenticator(userLocalDataSource, refreshApiService, networkHelper)

    @Provides
    @Singleton
    fun provideJwtAuthInterceptor(
        userLocalDataSource: UserLocalDataSource,
        refreshApiService: RefreshApiService,
        networkHelper: NetworkHelper
    ): JwtAuthInterceptor = JwtAuthInterceptor(userLocalDataSource, refreshApiService, networkHelper)

}