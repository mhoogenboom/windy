package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.position.Position
import java.io.File

interface View {

    fun setTitle(title: String)

    fun enableMenu(enabled: Boolean)

    fun setBoard(position: Position, upsideDown: Boolean = false)

    fun setHistory(moves: String)

    fun enableNextMove(onNextMoveRequired: (() -> Unit)?)

    fun enableMovesOnBoard(onActionEntered: ((Action) -> Boolean)?)

    fun enableAcceptDraw(onActionEntered: ((Action) -> Boolean)?)

    fun enableResign(onActionEntered: ((Action) -> Boolean)?)

    fun enterGameDetails(onGameDetailsEntered: (String, String) -> Unit)

    fun showMessage(message: String)

    fun showOpenDialog(): File?

    fun showSaveDialog(): File?
}