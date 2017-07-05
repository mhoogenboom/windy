package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.text.GameReader
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.getString
import java.util.*
import javax.swing.JMenuItem

class ImportGamesController(private val view: View, private val texts: ResourceBundle, private val db: Database) {

    fun attachToMenu(): JMenuItem {
        val menuItem = JMenuItem(texts.getString("import_games.menu"))
        menuItem.addActionListener { onStart() }
        return menuItem
    }

    fun onStart() {
        view.enableMenu(false)
        view.setTitle(texts.getString("import_games.title"))

        val file = view.showOpenDialog()
        if (file != null) {
            val games = GameReader().read(file)

            for (game in games) {
                db.store(game)
            }

            view.showMessage(texts.getString("import_games.done", games.size))
        }

        view.setTitle(texts.getString("app.welcome"))
        view.enableMenu(true)
    }
}