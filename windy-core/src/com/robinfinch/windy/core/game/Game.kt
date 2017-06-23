package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Generator
import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position

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

    private val position = Position()

    fun start() {
        for (square in 1..20) {
            position.empty[square] = false
            position.white[square] = false
            position.king[square] = false
        }

        for (square in 21..30) {
            position.empty[square] = true
        }

        for (square in 31..50) {
            position.empty[square] = false
            position.white[square] = true
            position.king[square] = false
        }
    }

    fun whitesTurn() = position.white[0]

    fun validMoves() = Generator(position).generate()

    fun execute(move: Move) {
        history.add(move)
        position.execute(move)
    }

    fun history() = ArrayList<Move>(history)

    fun position() = position.copy()
}