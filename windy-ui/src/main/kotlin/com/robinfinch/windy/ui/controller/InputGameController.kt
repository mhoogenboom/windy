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
        view.enableMainMenu(false)

        view.enterGameDetails(this::onGameDetailsEntered)
    }

    private fun onGameDetailsEntered(optDetails: Optional<GameDetails>) {

        if (optDetails.isPresent) {
            with(optDetails.get()) {
                arbiter.setupGame()
                arbiter.white = this.white
                arbiter.black = this.black
                arbiter.event = this.event
                arbiter.date = this.date
            }
            view.setGames(listOf(arbiter.currentGame))
            view.setBoard(arbiter.currentPosition)

            view.enableMovesOnBoard(this::onActionEntered)
            view.enableAcceptDraw(this::onActionEntered)
            view.enableResign(this::onActionEntered)

            whiteHasTheBoard = true
            play()
        } else {
            view.enableMainMenu(true)
        }
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

            view.setBoard(arbiter.currentPosition)
            view.setHistory(game.moves().format(html = true))
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
                    view.enableMainMenu(true)
                }
    }
}