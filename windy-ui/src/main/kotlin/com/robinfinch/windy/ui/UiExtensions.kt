package com.robinfinch.windy.ui

import java.awt.Font
import java.awt.event.ActionEvent
import java.text.MessageFormat
import java.util.*
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JList
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

