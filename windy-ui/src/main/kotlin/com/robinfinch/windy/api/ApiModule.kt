package com.robinfinch.windy.api

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    @Named("url")
    fun providesUrl() = "http://localhost:8080"

    @Provides
    @Singleton
    fun providesApi(@Named("url") url: String): WindyApi {

        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(WindyApi::class.java)
    }
}