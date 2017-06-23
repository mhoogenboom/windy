package com.robinfinch.windy.core.position

class Position() {

    companion object {
        const val NUMBER_OF_SQUARES = 50
    }

    val empty = Array<Boolean>(1 + NUMBER_OF_SQUARES, { it > 0 })
    val white = Array<Boolean>(1 + NUMBER_OF_SQUARES, { it == 0 })
    val king = Array<Boolean>(1 + NUMBER_OF_SQUARES, { false })

    fun execute(move: Move) {
        empty[move.start] = true

        for (step in move.steps) {
            empty[step] = true
        }

        empty[move.end] = false
        white[move.end] = white[move.start]
        king[move.end] = king[move.start]

        white[0] = !white[0]
    }

    fun copy(): Position {
        val copy = Position()
        System.arraycopy(empty, 0, copy.empty, 0, 1 + NUMBER_OF_SQUARES);
        System.arraycopy(white, 0, copy.white, 0, 1 + NUMBER_OF_SQUARES);
        System.arraycopy(king, 0, copy.king, 0, 1 + NUMBER_OF_SQUARES);
        return copy
    }
}