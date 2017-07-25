package com.robinfinch.windy.api

import com.robinfinch.windy.core.game.Player
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface WindyApi {

    @POST("/game/{gameId}/white")
    fun connectWhiteToGame(@Path("gameId") gameId: String, @Body player: Player): Observable<Unit>

    @POST("/game/{gameId}/black")
    fun connectBlackToGame(@Path("gameId") gameId: String, @Body player: Player): Observable<Unit>

}