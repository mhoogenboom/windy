package com.robinfinch.windy.app

import com.robinfinch.windy.ui.controller.*
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

    @Inject
    lateinit var localPlayController: LocalPlayController

    @Inject
    lateinit var inputGameController: InputGameController

    @Inject
    lateinit var importGamesController: ImportGamesController

    @Inject
    lateinit var replayGamesController: ReplayGamesController

    @Inject
    lateinit var inputExerciseController: InputExerciseController

    @Inject
    lateinit var frame: View

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun start() {
        frame.show(
                localPlayController.attachToMenu(),
                inputGameController.attachToMenu(),
                importGamesController.attachToMenu(),
                replayGamesController.attachToMenu(),
                inputExerciseController.attachToMenu()
        )
    }
}