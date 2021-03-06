package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.storage.Storable

class Game() : Storable {

    enum class Result {
        UNKNOWN, WHITE_WIN, DRAW, BLACK_WIN
    }

    override var id = 0L
    var white: String = ""
    var black: String = ""
    var event: String = ""
    var date: String = ""
    var result = Result.UNKNOWN

    private val history = mutableListOf<Move>()

    fun execute(move: Move) {
        history.add(move)
    }

    fun move(number: Int) = history.getOrNull(number)

    fun moves() = ArrayList<Move>(history)

    fun copy() = this // todo
}