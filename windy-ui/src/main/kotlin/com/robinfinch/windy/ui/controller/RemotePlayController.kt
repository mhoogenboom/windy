package com.robinfinch.windy.ui.controller

import com.google.gson.Gson
import com.robinfinch.windy.api.WindyApi
import com.robinfinch.windy.api.ifCodeElse
import com.robinfinch.windy.api.ifNoContentElse
import com.robinfinch.windy.core.game.Arbiter
import com.robinfinch.windy.core.game.Player
import com.robinfinch.windy.core.game.RemoteGameStatus
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.GameDetails
import com.robinfinch.windy.ui.edt
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection
import java.time.LocalDate
import java.util.*
import javax.swing.JMenu
import javax.swing.JMenuItem

class RemotePlayController(private val view: View, private val texts: ResourceBundle, private val api: WindyApi, private val db: Database) {

    private val arbiter = Arbiter()

    fun attachToMenu(): JMenu {

        val playWhite = JMenuItem(texts.getString("remote_play.play_white"))
        playWhite.addActionListener { connectWithWhite() }

        val playBlack = JMenuItem(texts.getString("remote_play.play_black"))
        playBlack.addActionListener { connectWithBlack() }

        val menu = JMenu(texts.getString("remote_play.menu"))
        menu.add(playWhite)
        menu.add(playBlack)
        return menu
    }

    private fun connectWithWhite() {
        view.enableMainMenu(false)

        view.enterGameDetails(this::onWhiteDetailsEntered, black = "...", date = LocalDate.now().toString())
    }

    private fun onWhiteDetailsEntered(optDetails: Optional<GameDetails>) {

        if (optDetails.isPresent()) {
            setUpArbiter(optDetails.get())

            val player = Player()
            player.name = arbiter.white

            api.connectWhiteToGame(arbiter.event, player)
                    .subscribeOn(Schedulers.io())
                    .observeOn(edt())
                    .subscribe(
                            ifNoContentElse(
                                    this::checkStatus,
                                    this::onConnectionError),
                            {e -> onConnectionError(e.message) }
                    )
        } else {
            view.enableMainMenu(true)
        }
    }

    private fun connectWithBlack() {
        view.enableMainMenu(false)

        view.enterGameDetails(this::onBlackDetailsEntered, white = "...", date = LocalDate.now().toString())
    }

    private fun onBlackDetailsEntered(optDetails: Optional<GameDetails>) {

        if (optDetails.isPresent()) {
            setUpArbiter(optDetails.get())

            val player = Player()
            player.name = arbiter.black

            api.connectBlackToGame(arbiter.event, player)
                    .subscribeOn(Schedulers.io())
                    .observeOn(edt())
                    .subscribe(
                            ifNoContentElse(
                                    this::checkStatus,
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

    private fun checkStatus() {

        Thread.sleep(600);

        api.getStatus(arbiter.event)
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe(
                        ifCodeElse<RemoteGameStatus>(HttpURLConnection.HTTP_OK,
                                this::onStatusChecked,
                                this::onConnectionError),
                        { e -> onConnectionError(e.message) }
                )
    }

    private fun onStatusChecked(response: RemoteGameStatus) {

        if (response.white.isNotBlank() && response.black.isNotBlank()) {
            arbiter.white = response.white
            arbiter.black = response.black
            view.setGames(listOf(arbiter.currentGame))

            view.enableMainMenu(true) // todo
        } else {
            checkStatus()
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


/*
        if (optDetails.isPresent) {
            val details = optDetails.get()

            arbiter.setupGame()
            arbiter.white = details.white
            arbiter.black = details.black
            arbiter.event = details.event
            arbiter.date = details.date

            view.setGames(listOf(arbiter.currentGame))
            view.setBoard(arbiter.currentPosition)

            view.enableMovesOnBoard(this::onActionEntered)
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

        when (game.result) {

            Game.Result.UNKNOWN -> {
                view.setBoard(arbiter.currentPosition)
                view.setBoardUpsideDown(!whiteHasTheBoard)
                view.setHistory(game.moves().format(html = true))
                view.enableAcceptDraw(if (arbiter.drawProposed) this::onActionEntered else null)

                val player = if (whiteHasTheBoard) arbiter.white else arbiter.black
                view.showMessage(texts.getString("local_play.has_the_board", player))
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

        view.disableBoard()
        view.enableAcceptDraw(null)
        view.enableResign(null)

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
                */


}