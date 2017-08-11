package com.robinfinch.windy.api

import com.robinfinch.windy.core.game.Player
import com.robinfinch.windy.core.game.RemoteGameStatus
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WindyApi {

    @POST("/game/{gameId}/white")
    fun connectWhiteToGame(@Path("gameId") gameId: String, @Body player: Player): Observable<Response<Unit>>

    @POST("/game/{gameId}/black")
    fun connectBlackToGame(@Path("gameId") gameId: String, @Body player: Player): Observable<Response<Unit>>

    @GET("/game/{gameId}/status")
    fun getStatus(@Path("gameId") gameId: String): Observable<Response<RemoteGameStatus>>
}