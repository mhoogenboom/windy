package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.game.Arbiter
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.format
import java.io.BufferedWriter
import java.io.StringWriter
import java.util.*

class InputGameController(private val view: View, private val texts: ResourceBundle) {

    private val arbiter: Arbiter = Arbiter()

    private var whiteHasTheBoard: Boolean = true

    fun onStart() {
        view.enableMenu(false)

        arbiter.setupGame()

        view.setTitle(texts.getString("app.setting_up"))
        view.setBoard(arbiter.currentPosition)

        view.enterGameDetails(this::onGameDetailsEntered)
    }

    fun onGameDetailsEntered(white: String, black: String) {
        arbiter.white = white
        arbiter.black = black

        view.setTitle("Entering ${white} - ${black}")
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

        if (arbiter.result == Game.Result.UNKNOWN) {
            val history = StringWriter()
            BufferedWriter(history).use { out -> arbiter.history.format(out, html = true) }

            view.setBoard(arbiter.currentPosition)
            view.setHistory(history.toString())
        } else {
            saveGame()
        }
    }

    private fun saveGame() {

        view.enableMovesOnBoard(null)
        view.enableAcceptDraw(null)
        view.enableResign(null)

        val file = view.showSaveDialog()
        if (file != null) {
            arbiter.writeGame(file)
        }

        view.setBoard(Position(), false)
        view.setHistory("")
        view.enableMenu(true)
    }
}