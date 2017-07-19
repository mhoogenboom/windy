package com.robinfinch.windy.core.text

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import io.reactivex.Observable
import java.io.File
import java.net.URL

class GameReader {

    companion object {

        val TAG_EVENT = "event"
        val TAG_DATE = "date"
        val TAG_WHITE = "white"
        val TAG_BLACK = "black"
        val TAG_RESULT = "result"

        private val TAG_REGEX = Regex("\\[(.+) \"(.+)\"\\]")

        private val MOVE_REGEX = Regex("([0-9]+\\.)|([0-9]+-[0-9]+)|([0-9]+(x[0-9]+)+)");
    }

    private val position = Position()

    private var game = Game()

    private var readingHeader = true

    fun read(url: URL): Observable<List<Game>> =

            Observable.fromCallable {

                val games = mutableListOf<Game>()

                url.openStream().use { inputStream ->
                    inputStream.bufferedReader().forEachLine {
                        parseLine(games, it)
                    }
                }

                games
            }

    fun read(file: File): Observable<List<Game>> =

        Observable.fromCallable {
            val games = mutableListOf<Game>()

            position.start()

            file.forEachLine {
                parseLine(games, it)
            }

            games.add(game)

            games
        }

    private fun parseLine(games: MutableList<Game>, it: String) {

        when {
            it.isBlank() -> {
                // blank
            }
            it.startsWith('%') -> {
                // comment
            }
            it.startsWith('[') -> {
                parseTag(games, it)
            }
            else -> {
                parseMoves(it)
            }
        }
    }

    private fun parseTag(games: MutableList<Game>, text: String) {

        if (!readingHeader) {
            games.add(game)

            position.start()
            game = Game()

            readingHeader = true
        }

        val groups = TAG_REGEX.matchEntire(text)?.groupValues

        if (groups != null) {
            val name = groups[1].trim().toLowerCase()
            val value = groups[2]

            when (name) {
                TAG_EVENT -> game.event = value
                TAG_DATE -> game.date = value
                TAG_WHITE -> game.white = value
                TAG_BLACK -> game.black = value
                TAG_RESULT -> game.result = Game.Result.values().find { it.format() == value } ?: Game.Result.UNKNOWN
            }
        }
    }

    private fun parseMoves(text: String) {

        readingHeader = false

        MOVE_REGEX.findAll(text).forEach {

            val groups = it.groupValues

            when {
                groups[2].isNotBlank() -> {
                    val move = groups[2]

                    val moves = position.validMoves().filter {
                        it.format() == move
                    }

                    if (moves.size == 1) {
                        position.execute(moves[0])
                        game.execute(moves[0])
                    }
                }
                groups[3].isNotBlank() -> {
                    val hit = groups[3]

                    val hits = position.validMoves().filter {
                        (it.format() == hit) || (it.formatWithSteps() == hit)
                    }

                    if (hits.size == 1) {
                        position.execute(hits[0])
                        game.execute(hits[0])
                    }
                }
            }
        }
    }
}

