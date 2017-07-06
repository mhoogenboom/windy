package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.format
import com.robinfinch.windy.db.Database
import java.io.BufferedWriter
import java.io.StringWriter
import java.util.*
import javax.swing.JMenuItem

class ReplayGamesController(private val view: View, private val texts: ResourceBundle, private val db: Database) {

    private val position = Position()

    private lateinit var currentGame: Game

    private var currentMove: Int = 0

    fun attachToMenu(): JMenuItem {
        val menuItem = JMenuItem(texts.getString("replay_games.menu"))
        menuItem.addActionListener { onStart() }
        return menuItem
    }

    fun onStart() {
        view.setTitle(texts.getString("replay_games.setting_up"))

        view.enterSearchCriteria {query ->
            val games = db.findByPlayer(query)
            view.setGames(games)

            view.enableSelectGame(this::onGameSelected)
        }
    }

    private fun onGameSelected(game: Game) {

        position.start()

        view.setTitle("${game.white} - ${game.black} (${game.event}, ${game.date})")
        view.enableMenu(false)
        view.enableNextMove(this::onNextMoveRequested)

        currentGame = game
        currentMove = 0
        play()

    }

    private fun onNextMoveRequested() {
        val move = currentGame.move(currentMove)

        if (move != null) {
            position.execute(move)

            currentMove++
            play()
        } else {
            val message = when (currentGame.result) {
                Game.Result.WHITE_WIN -> texts.getString("replay_games.white_win")
                Game.Result.BLACK_WIN -> texts.getString("replay_games.black_win")
                Game.Result.DRAW -> texts.getString("replay_games.draw")
                Game.Result.UNKNOWN -> texts.getString("replay_games.unknown_result")
            }

            view.enableNextMove(null)
            view.showMessage(message)

            view.enableMenu(true)
            view.setTitle(texts.getString("replay_games.setting_up"))
        }
    }

    private fun play() {

        val history = StringWriter()
        BufferedWriter(history).use { out -> currentGame.moves().format(out, html = true, emphasize = currentMove) }

        view.setBoard(position)
        view.setHistory(history.toString())
    }
}