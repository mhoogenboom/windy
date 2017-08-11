package com.robinfinch.windy.api

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
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

fun ifNoContentElse(pos: () -> Unit, neg: (String?) -> Unit): (Response<Unit>) -> Unit =
        { response ->
            if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                pos()
            } else {
                val errorBody = response.errorBody()
                if (errorBody == null) {
                    neg(null)
                } else {
                    val error = Gson().fromJson(errorBody.string(), Error::class.java)
                    neg(error.message)
                }
            }
        }

fun <T> ifCodeElse(code: Int, pos: (T) -> Unit, neg: (String?) -> Unit): (Response<T>) -> Unit  =
        { response ->
            if (response.code() == code) {
                val responseBody = response.body()
                if (responseBody == null) {
                    neg(null)
                } else {
                    pos(responseBody)
                }
            } else {
                val errorBody = response.errorBody()
                if (errorBody == null) {
                    neg(null)
                } else {
                    val error = Gson().fromJson(errorBody.string(), Error::class.java)
                    neg(error.message)
                }
            }
        }