package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Arbiter
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.game.GameAction
import com.robinfinch.windy.core.text.format
import java.io.BufferedWriter
import java.io.StringWriter
import java.util.*

class WindyController(private val view: View, private val texts: ResourceBundle) {

    private val arbiter: Arbiter = Arbiter()

    private var whiteHasTheBoard: Boolean = true

    fun onStart() {
        arbiter.setupGame()

        view.setTitle(texts.getString("app.setting_up"))
        view.setBoard(arbiter.currentPosition, false)
        view.setHistory("")

        view.enterGameDetails()
    }

    fun onGameDetailsEntered(white: String, black: String) {
        arbiter.white = white
        arbiter.black = black

        view.setTitle("${white} - ${black}")

        whiteHasTheBoard = true
        play()
    }

    fun onActionEntered(action: GameAction): Boolean {

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

        when (arbiter.result) {

            Game.Result.UNKNOWN -> {
                val history = StringWriter()
                BufferedWriter(history).use { out -> arbiter.currentHistory.format(out) }

                view.setBoard(arbiter.currentPosition, !whiteHasTheBoard)
                view.setHistory(history.toString())
                view.enableAcceptDraw(arbiter.drawProposed)

                val player = if (whiteHasTheBoard) arbiter.white else arbiter.black
                view.showMessage("The board is yours, ${player}")
            }

            Game.Result.WHITE_WIN -> {
                view.showMessage("Congratulations ${arbiter.white} with your win")
                saveGame()
            }

            Game.Result.BLACK_WIN -> {
                view.showMessage("Congratulations ${arbiter.black} with your win")
                saveGame()
            }

            Game.Result.DRAW -> {
                view.showMessage("It's a draw!")
                saveGame()
            }
        }
    }

    private fun saveGame() {

        val file = view.showSaveDialog()
        if (file != null) {
            arbiter.writeGame(file)
        }

        onStart()
    }
}