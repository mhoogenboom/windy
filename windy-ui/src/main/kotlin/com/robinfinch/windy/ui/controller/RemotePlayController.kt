package com.robinfinch.windy.ui.controller

import com.google.gson.Gson
import com.robinfinch.windy.api.StatusChecker
import com.robinfinch.windy.api.WindyApi
import com.robinfinch.windy.api.ifCodeElse
import com.robinfinch.windy.api.ifNoContentElse
import com.robinfinch.windy.core.game.*
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.format
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.GameDetails
import com.robinfinch.windy.ui.edt
import com.robinfinch.windy.ui.getString
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection
import java.time.LocalDate
import java.util.*
import javax.swing.JMenu
import javax.swing.JMenuItem

class RemotePlayController(private val view: View, private val texts: ResourceBundle, private val api: WindyApi, private val db: Database) {

    private val statusChecker = StatusChecker(api)

    private val arbiter = Arbiter()

    private var playWithWhite = true

    fun attachToMenu(): JMenu {

        val playWhite = JMenuItem(texts.getString("remote_play.play_white"))
        playWhite.addActionListener {
            playWithWhite = true
            enterDetails()
        }

        val playBlack = JMenuItem(texts.getString("remote_play.play_black"))
        playBlack.addActionListener {
            playWithWhite = false
            enterDetails()
        }

        val menu = JMenu(texts.getString("remote_play.menu"))
        menu.add(playWhite)
        menu.add(playBlack)
        return menu
    }

    private fun enterDetails() {
        view.enableMainMenu(false)

        view.setBoardUpsideDown(!playWithWhite)

        if (playWithWhite) {
            view.enterGameDetails(this::onDetailsEntered, black = "...", date = LocalDate.now().toString())
        } else {
            view.enterGameDetails(this::onDetailsEntered, white = "...", date = LocalDate.now().toString())
        }
    }

    private fun onDetailsEntered(optDetails: Optional<GameDetails>) {

        if (optDetails.isPresent()) {
            setUpArbiter(optDetails.get())

            val player = Player()

            if (playWithWhite) {
                player.name = arbiter.white
                api.connectWhiteToGame(arbiter.event, player)
            } else {
                player.name = arbiter.black
                api.connectBlackToGame(arbiter.event, player)
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(edt())
                    .subscribe(
                            ifNoContentElse(
                                    this::onConnectedToGame,
                                    this::onConnectionError),
                            {e -> onConnectionError(e.message) }
                    )
        } else {
            view.enableMainMenu(true)
        }
    }

    private fun setUpArbiter(details: GameDetails) {

        arbiter.setupGame()
        arbiter.white = details.white
        arbiter.black = details.black
        arbiter.event = details.event
        arbiter.date = details.date

        view.setGames(listOf(arbiter.currentGame))
        view.setBoard(arbiter.currentPosition)
    }

    private fun onConnectedToGame() {
        statusChecker.checkStatus(arbiter.event, this::onStatusChecked, this::onConnectionError)
    }

    private fun onStatusChecked(response: RemoteGameStatus) {

        if (response.white.isNotBlank() && response.black.isNotBlank()) {
            arbiter.white = response.white
            arbiter.black = response.black
            view.setGames(listOf(arbiter.currentGame))

            if (playWithWhite) {
                enableMoving()
            } else {
                // waitForOpponent()
            }
        } else {
            onConnectedToGame()
        }
    }

    private fun onConnectionError(message: String?) {

        if ((message == null) || message.isBlank()) {
            view.showMessage(texts.getString("app.general_error"))
        } else {
            view.showMessage(message)
        }

        view.setGames(emptyList())
        view.enableMainMenu(true)
    }

    private fun onActionEntered(action: Action): Boolean {

        val accepted = if (playWithWhite) arbiter.acceptWhite(action) else arbiter.acceptBlack(action)

        if (accepted) {
            sendAction(action)
            play(true)
            return true
        } else {
            return false
        }
    }

    private fun sendAction(action: Action) {
        // todo
    }

    private fun onActionReceived(action: Action): Boolean {

        val accepted = if (playWithWhite) arbiter.acceptBlack(action) else arbiter.acceptWhite(action)

        if (accepted) {
            play(false)
            return true
        } else {
            return false
        }
    }

    private fun play(forOpponent: Boolean) {

        val game = arbiter.currentGame

        when (game.result) {

            Game.Result.UNKNOWN -> {
                view.setBoard(arbiter.currentPosition)
                view.setHistory(game.moves().format(html = true))

                if (forOpponent) {
                    disableMoving()
                } else {
                    enableMoving()
                }
            }

            Game.Result.WHITE_WIN -> {
                finish(texts.getString("local_play.win", arbiter.white))
            }

            Game.Result.BLACK_WIN -> {
                finish(texts.getString("local_play.win", arbiter.black))
            }

            Game.Result.DRAW -> {
                finish(texts.getString("local_play.draw"))
            }
        }
    }

    private fun finish(message: String) {

        disableMoving()

        view.showMessage(message)

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

    private fun enableMoving() {
        view.enableMovesOnBoard(this::onActionEntered)
        view.enableAcceptDraw(if (arbiter.drawProposed) this::onActionEntered else null)
        view.enableResign(this::onActionEntered)
    }

    private fun disableMoving() {
        view.disableBoard()
        view.enableAcceptDraw(null)
        view.enableResign(null)
    }
}