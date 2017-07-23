package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.game.Query
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.ui.GameDetails
import com.robinfinch.windy.ui.ListSelection
import java.io.File
import java.util.*
import javax.swing.JMenuItem

interface View {

    fun show(vararg plugins: JMenuItem)

    fun enableMainMenu(enabled: Boolean)

    fun enableBoardMenu(enabled: Boolean)

    fun setGames(games: List<Game>)

    fun enableSelectGame(onGameSelected: ((ListSelection<Game>) -> Unit)?)

    fun setBoard(position: Position)

    fun getBoard(): Position

    fun setBoardUpsideDown(upsideDown: Boolean)

    fun setHistory(moves: String)

    fun enableNextMove(onNextMoveRequired: (() -> Unit)?)

    fun enableSettingUpOnBoard()

    fun enableMovesOnBoard(onActionEntered: (Action) -> Boolean)

    fun disableBoard()

    fun enableAcceptDraw(onActionEntered: ((Action) -> Boolean)?)

    fun enableResign(onActionEntered: ((Action) -> Boolean)?)

    fun enterGameDetails(date: String, onGameDetailsEntered: (Optional<GameDetails>) -> Unit)

    fun enterSearchCriteria(onSearchCriteriaEntered: (Optional<Query>) -> Unit)

    fun showMessage(message: String)

    fun showInputDialog(title: String, message: String): String?

    fun showOpenDialog(): File?

    fun showSaveDialog(): File?
}