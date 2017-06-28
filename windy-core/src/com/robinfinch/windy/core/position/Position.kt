package com.robinfinch.windy.core.position

class Position() {

    companion object {
        val NUMBER_OF_SQUARES = 50
    }

    internal val empty = Array<Boolean>(1 + NUMBER_OF_SQUARES, { it > 0 })
    internal val white = Array<Boolean>(1 + NUMBER_OF_SQUARES, { it == 0 })
    internal val king = Array<Boolean>(1 + NUMBER_OF_SQUARES, { false })

    fun start() {
        for (square in 1..20) {
            empty[square] = false
            white[square] = false
            king[square] = false
        }

        for (square in 21..30) {
            empty[square] = true
        }

        for (square in 31..50) {
            empty[square] = false
            white[square] = true
            king[square] = false
        }
    }

    fun validMoves() = Generator(this).generate()

    fun execute(move: Move) {
        empty[move.start] = true

        for (step in move.steps) {
            empty[step] = true
        }

        empty[move.end] = false
        white[move.end] = white[move.start]
        king[move.end] = king[move.start] || (move.end in promotionRow())

        white[0] = !white[0]
    }

    fun promotionRow() = if (white[0]) 1..5 else 46..50

    fun copy(): Position {
        val copy = Position()
        System.arraycopy(empty, 0, copy.empty, 0, 1 + NUMBER_OF_SQUARES);
        System.arraycopy(white, 0, copy.white, 0, 1 + NUMBER_OF_SQUARES);
        System.arraycopy(king, 0, copy.king, 0, 1 + NUMBER_OF_SQUARES);
        return copy
    }
}