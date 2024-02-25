package ru.gidevent.androidapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ru.gidevent.androidapp.data.service.AdvertisementApiService
import ru.gidevent.androidapp.data.service.RefreshApiService
import ru.gidevent.androidapp.data.service.UserApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideApiService(@AuthRetrofit retrofit: Retrofit): UserApiService = retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideRefreshApiService(@PublicRetrofit retrofit: Retrofit): RefreshApiService = retrofit.create(RefreshApiService::class.java)

    @Provides
    @Singleton
    fun provideAdvertisementApiService(@PublicRetrofit retrofit: Retrofit): AdvertisementApiService = retrofit.create(AdvertisementApiService::class.java)
}