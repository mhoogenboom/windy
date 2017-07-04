package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Position

interface Storage {

    fun store(game: Game)

    fun findByPlayer(name: String, includeWhite: Boolean = true, includeBlack: Boolean = true): List<Game>

    fun findByPosition(position: Position): List<Game>
}