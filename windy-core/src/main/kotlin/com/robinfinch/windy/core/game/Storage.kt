package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Position

interface Storage {

    fun store(game: Game)

    fun findByPlayer(query: Query): List<Game>

    fun findByPosition(position: Position): List<Game>
}

class Query(val player: String, val withWhite: Boolean = true, val withBlack: Boolean = true) {

}