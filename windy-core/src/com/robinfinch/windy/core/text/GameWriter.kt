package com.robinfinch.windy.core.text

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import java.io.BufferedWriter
import java.io.File

class GameWriter {

    fun write(file: File, games: List<Game>) {

        file.bufferedWriter().use {out ->
            games.forEach {
                write(out, it)
            }
        }
    }

    private fun write(out: BufferedWriter, game: Game) {

        writeTag(out, GameReader.TAG_EVENT, game.event)
        writeTag(out, GameReader.TAG_DATE, game.date)
        writeTag(out, GameReader.TAG_WHITE, game.white)
        writeTag(out, GameReader.TAG_BLACK, game.black)
        writeTag(out, GameReader.TAG_RESULT, game.result.format())

        val position = Position()
        position.start()

        var ply = 0

        for (move in game.history()) {
            if (ply % 2 == 0) {
                if (ply % 8 == 0) {
                    out.newLine()
                }
                out.write("${1 + ply / 2}.")
            }

            val shortForm = move.format()

            val duplicates = position.validMoves()
                    .map(Move::format)
                    .count {it == shortForm}

            out.write(if (duplicates == 1) shortForm else move.formatWithSteps())
            out.write(" ")

            position.execute(move)
            ply++
        }
    }

    private fun writeTag(out: BufferedWriter, name: String, value: String) {

        if (value.isNotBlank()) {
            out.write("[${name} \"${value}\"]")
            out.newLine()
        }
    }
}