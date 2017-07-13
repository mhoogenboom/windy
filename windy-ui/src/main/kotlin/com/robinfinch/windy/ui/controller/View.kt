package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.game.Query
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.ui.GameDetails
import com.robinfinch.windy.ui.ListSelection
import java.io.File
import javax.swing.JMenuItem

interface View {

    fun show(vararg plugins: JMenuItem)

    fun enableMenu(enabled: Boolean)

    fun setGames(games: List<Game>)

    fun enableSelectGame(onGameSelected: ((ListSelection<Game>) -> Unit)?)

    fun setBoard(position: Position, upsideDown: Boolean = false)

    fun setHistory(moves: String)

    fun enableNextMove(onNextMoveRequired: (() -> Unit)?)

    fun enableSettingUpOnBoard()

    fun enableMovesOnBoard(onActionEntered: (Action) -> Boolean)

    fun disableBoard()

    fun enableAcceptDraw(onActionEntered: ((Action) -> Boolean)?)

    fun enableResign(onActionEntered: ((Action) -> Boolean)?)

    fun enterGameDetails(date: String, onGameDetailsEntered: (GameDetails) -> Unit)

    fun enterSearchCriteria(onSearchCriteriaEntered: (Query) -> Unit)

    fun showMessage(message: String)

    fun showOpenDialog(): File?

    fun showSaveDialog(): File?
}