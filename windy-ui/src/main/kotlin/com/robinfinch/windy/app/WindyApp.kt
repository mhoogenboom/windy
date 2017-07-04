package com.robinfinch.windy.app

import com.robinfinch.windy.ui.MainFrame
import com.robinfinch.windy.ui.controller.InputGameController
import com.robinfinch.windy.ui.controller.LocalPlayController
import com.robinfinch.windy.ui.controller.View
import java.util.*
import javax.inject.Inject
import javax.swing.JMenuItem
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
    lateinit var frame: View

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun start() {
        frame.show(localPlayController.attachToMenu(), inputGameController.attachToMenu())
    }
}