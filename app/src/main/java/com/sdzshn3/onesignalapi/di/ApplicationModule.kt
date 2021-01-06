package com.sdzshn3.onesignalapi.di

import com.sdzshn3.onesignalapi.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @OneSignal
    @Provides
    @Singleton
    fun provideOneSignalRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://onesignal.com/api/v1/")
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @FreeImageHost
    @Provides
    @Singleton
    fun provideFreeImageHostRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://freeimage.host/")
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @OneSignal
    @Provides
    @Singleton
    fun provideOneSignalApiService(@OneSignal retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @FreeImageHost
    @Provides
    @Singleton
    fun provideFreeImageHostApiService(@FreeImageHost retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}