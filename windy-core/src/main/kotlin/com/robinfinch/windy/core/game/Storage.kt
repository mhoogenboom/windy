package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Position
import io.reactivex.Observable

interface Storage {

    fun store(game: Game): Observable<Unit>

    fun findByPlayer(query: Query): Observable<List<Game>>

    fun findByPosition(position: Position): Observable<List<Game>>
}

class Query(val player: String, val withWhite: Boolean = true, val withBlack: Boolean = true, val position: Position) {

}