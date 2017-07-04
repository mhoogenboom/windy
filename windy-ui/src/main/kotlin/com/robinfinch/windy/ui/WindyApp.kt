package com.robinfinch.windy.ui

import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.controller.InputGameController
import com.robinfinch.windy.ui.controller.LocalPlayController
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main(args: Array<String>) {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeLater {
        WindyApp().start()
    }
}

class WindyApp {

    private val localPlayController: LocalPlayController
    private val inputGameController: InputGameController

    private val frame: MainFrame

    @Inject
    lateinit var texts: ResourceBundle

    init {
        DaggerAppComponent.builder().build().inject(this)

        frame = MainFrame(texts, this::onStartLocalPlay, this::onStartInputGame)

        val db = Database(File("data"))

        localPlayController = LocalPlayController(frame, texts, db)

        inputGameController = InputGameController(frame, texts, db)
    }

    fun start() {
        frame.show()
    }

    private fun onStartLocalPlay() {
        localPlayController.onStart()
    }

    private fun onStartInputGame() {
        inputGameController.onStart()
    }
}