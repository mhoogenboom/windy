package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.game.Arbiter
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.format
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.GameDetails
import com.robinfinch.windy.ui.edt
import io.reactivex.schedulers.Schedulers
import java.io.BufferedWriter
import java.io.StringWriter
import java.util.*
import javax.swing.JMenuItem

class InputGameController(private val view: View, private val texts: ResourceBundle, private val db: Database) {

    private val arbiter: Arbiter = Arbiter()

    private var whiteHasTheBoard: Boolean = true

    fun attachToMenu(): JMenuItem {
        val menuItem = JMenuItem(texts.getString("input_game.menu"))
        menuItem.addActionListener { start() }
        return menuItem
    }

    private fun start() {
        view.enableMenu(false)

        view.enterGameDetails("", this::onGameDetailsEntered)
    }

    private fun onGameDetailsEntered(details: GameDetails) {

        arbiter.setupGame()
        arbiter.white = details.white
        arbiter.black = details.black
        arbiter.event = details.event
        arbiter.date = details.date

        view.setGames(listOf(arbiter.currentGame))
        view.setBoard(arbiter.currentPosition)

        view.enableMovesOnBoard(this::onActionEntered)
        view.enableAcceptDraw(this::onActionEntered)
        view.enableResign(this::onActionEntered)

        whiteHasTheBoard = true
        play()
    }

    private fun onActionEntered(action: Action): Boolean {

        if (whiteHasTheBoard) {
            if (arbiter.acceptWhite(action)) {
                whiteHasTheBoard = false
                play()
                return true
            } else {
                return false
            }
        } else {
            if (arbiter.acceptBlack(action)) {
                whiteHasTheBoard = true
                play()
                return true
            } else {
                return false
            }
        }
    }

    private fun play() {

        val game = arbiter.currentGame

        if (game.result == Game.Result.UNKNOWN) {
            val history = StringWriter()
            BufferedWriter(history).use { out -> game.moves().format(out, html = true) }

            view.setBoard(arbiter.currentPosition)
            view.setHistory(history.toString())
        } else {
            finish()
        }
    }

    private fun finish() {

        view.disableBoard()
        view.enableAcceptDraw(null)
        view.enableResign(null)

        arbiter.saveGame(db)
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe {
                    view.setGames(emptyList())
                    view.setBoard(Position())
                    view.setHistory("")
                    view.enableMenu(true)
                }
    }
}