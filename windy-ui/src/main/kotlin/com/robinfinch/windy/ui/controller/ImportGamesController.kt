package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.text.GameReader
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.edt
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.swing.JMenuItem

class ImportGamesController(private val view: View, private val texts: ResourceBundle, private val db: Database,
                            private val replayGamesController: ReplayGamesController) {

    fun attachToMenu(): JMenuItem {
        val menuItem = JMenuItem(texts.getString("import_games.menu"))
        menuItem.addActionListener { start() }
        return menuItem
    }

    fun start() {
        view.enableMenu(false)

        val file = view.showOpenDialog()
        if (file == null) {
            view.enableMenu(true)
        } else {
            GameReader().read(file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(edt())
                    .subscribe(this::importGames)
        }
    }

    private fun importGames(games: List<Game>) {

        db.storeGames(games)
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe { replayGamesController.start(games) }
    }
}