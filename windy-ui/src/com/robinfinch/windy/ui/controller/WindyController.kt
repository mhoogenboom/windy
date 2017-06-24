package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Arbiter
import com.robinfinch.windy.core.game.ExecuteMove
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.game.GameAction
import com.robinfinch.windy.core.position.Move

class WindyController(private val view: View) {

    private val arbiter: Arbiter = Arbiter()

    private var whiteHasTheBoard: Boolean = true

    fun onStart() {
        arbiter.setupGame()

        view.enterGameDetails()
    }

    fun onGameDetailsEntered(white: String, black: String) {
        arbiter.white = white
        arbiter.black = black

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
                view.enableAcceptDraw(arbiter.drawProposed)

                view.setBoard(arbiter.position, !whiteHasTheBoard)

                val player = if (whiteHasTheBoard) arbiter.white else arbiter.black

                view.showMessage("The board is yours, ${player}")
            }

            Game.Result.WHITE_WIN -> view.showMessage("Congratulations ${arbiter.white} with your win")

            Game.Result.BLACK_WIN -> view.showMessage("Congratulations ${arbiter.black} with your win")

            Game.Result.DRAW -> view.showMessage("It's a draw!")
        }
    }
}