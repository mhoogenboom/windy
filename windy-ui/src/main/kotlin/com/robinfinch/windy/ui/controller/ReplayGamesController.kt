package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.format
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.edt
import io.reactivex.schedulers.Schedulers
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
        menuItem.addActionListener { start() }
        return menuItem
    }

    private fun start() {
        view.enableMenu(false)

        view.enterSearchCriteria { query ->
            db.findByPlayer(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(edt())
                    .subscribe(this::start)
        }
    }

    fun start(games: List<Game>) {
        view.setGames(games)
        view.enableSelectGame(this::onGameSelected)
    }

    private fun onGameSelected(game: Game) {

        position.start()

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

            view.setBoard(Position())
            view.setHistory("")
        }
    }

    private fun play() {

        val history = StringWriter()
        BufferedWriter(history).use { out -> currentGame.moves().format(out, html = true, emphasize = currentMove) }

        view.setBoard(position)
        view.setHistory(history.toString())
    }
}
