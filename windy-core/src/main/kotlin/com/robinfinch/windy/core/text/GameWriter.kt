package com.robinfinch.windy.core.text

import com.robinfinch.windy.core.game.Game
import java.io.BufferedWriter
import java.io.File

class GameWriter {

    fun write(file: File, games: List<Game>) {

        file.bufferedWriter().use { out ->
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

        game.moves().formatTo(out, 8, true)
    }

    private fun writeTag(out: BufferedWriter, name: String, value: String) {

        if (value.isNotBlank()) {
            out.write("[${name} \"${value}\"]")
            out.newLine()
        }
    }

}