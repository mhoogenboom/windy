package com.robinfinch.windy.ui

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import java.awt.Font
import java.awt.event.ActionEvent
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.SwingUtilities
import javax.swing.event.ListSelectionEvent

fun JLabel.setBold() {
    font = Font(font.fontName, Font.BOLD, font.size)
}

fun JButton.disableWithoutActionListener() {
    if (isEnabled) {
        isEnabled = false
        removeActionListener(actionListeners[0])
    }
}

fun JButton.enableWithActionListener(listener: (ActionEvent) -> Unit) {
    disableWithoutActionListener()

    addActionListener(listener)
    isEnabled = true
}

fun <T> JList<T>.disableWithoutSelectionListener() {
    if (listSelectionListeners.size > 0) {
        removeListSelectionListener(listSelectionListeners[0])
    }
}

fun <T> JList<T>.enableWithSelectionListener(listener: (ListSelectionEvent) -> Unit) {
    disableWithoutSelectionListener()

    addListSelectionListener(listener)
}

fun ResourceBundle.getString(key: String, vararg params: Any): String =
        MessageFormat.format(getString(key), *params)

fun edt() = object : Scheduler() {

    override fun createWorker() = object : Worker() {
        override fun isDisposed() = false

        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            SwingUtilities.invokeAndWait(run)
            return this
        }

        override fun dispose() {

        }
    }
}
