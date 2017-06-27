package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Move

class Game() {

    enum class Result {
        UNKNOWN, WHITE_WIN, DRAW, BLACK_WIN
    }

    var white: String = ""
    var black: String = ""
    var event: String = ""
    var date: String = ""
    var result = Result.UNKNOWN

    private val history = mutableListOf<Move>()

    fun execute(move: Move) {
        history.add(move)
    }

    fun history() = ArrayList<Move>(history)
}