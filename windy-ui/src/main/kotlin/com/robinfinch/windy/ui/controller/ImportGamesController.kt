package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.text.GameReader
import com.robinfinch.windy.db.Database
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
        if (file != null) {
            val games = GameReader().read(file)

            for (game in games) {
                db.store(game)
            }

            replayGamesController.start(games)
        }
    }
}