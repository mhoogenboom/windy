package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.GameReader
import com.robinfinch.windy.core.text.format
import java.io.BufferedWriter
import java.io.StringWriter
import java.util.*

class ReplayController(private val view: View, private val texts: ResourceBundle) {

    private lateinit var games: List<Game>

    private var currentGame: Int = 0

    private val position = Position()

    private var currentMove: Int = 0

    fun onStart() {
        position.start()

        view.setTitle(texts.getString("app.setting_up"))
        view.setBoard(position)
        view.setHistory("")

        val file = view.showOpenDialog()
        if (file != null) {
            games = GameReader().read(file)

            currentGame = 0
            startGame()
        }
    }

    private fun startGame() {

        val game = games[currentGame]

        position.start()

        view.setTitle("${game.white} - ${game.black} (${game.event}, ${game.date})")
        view.setBoard(position)
        view.setHistory("")
        view.enableNextMove(this::onNextMoveRequested)

        currentMove = 0
        play()

    }

    private fun onNextMoveRequested() {
        val move = games[currentGame].move(currentMove)

        if (move != null) {
            position.execute(move)

            currentMove++
            play()
        } else {
            val message = when (games[currentGame].result) {
                Game.Result.WHITE_WIN -> texts.getString("replay.white_win")
                Game.Result.BLACK_WIN -> texts.getString("replay.black_win")
                Game.Result.DRAW -> texts.getString("replay.draw")
                Game.Result.UNKNOWN -> texts.getString("replay.unknown_result")
            }

            view.enableNextMove(null)
            view.showMessage(message)

            if (++currentGame < games.size) {
                startGame()
            } else {
                onStart()
            }
        }
    }

    private fun play() {

        val history = StringWriter()
        BufferedWriter(history).use { out -> games[currentGame].moves().format(out, html = true, emphasize = currentMove) }

        view.setBoard(position)
        view.setHistory(history.toString())
    }
}