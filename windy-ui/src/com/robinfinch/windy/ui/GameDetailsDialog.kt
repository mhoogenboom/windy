package com.robinfinch.windy.ui

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.*
import javax.swing.*

class GameDetailsDialog(parent: JFrame, texts: ResourceBundle)
    : JDialog(parent, texts.getString("game_details.title"), true) {

    private val whiteField = JTextField()

    private val blackField = JTextField()

    private val eventField = JTextField()

    private val dateField = JTextField()

    var onGameDetailsEntered: (GameDetails) -> Unit = { _ -> }

    init {
        layout = GridBagLayout()
        size = Dimension(300, 300)

        val gbc = GridBagConstraints()
        gbc.insets = Insets(10, 10, 0, 10)
        gbc.fill = GridBagConstraints.HORIZONTAL

        gbc.gridy = 0
        gbc.weighty = 1.0

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel(texts.getString("game_details.white")), gbc)

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(whiteField, gbc)

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel(texts.getString("game_details.black")), gbc)

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(blackField, gbc)

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel(texts.getString("game_details.event")), gbc)

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(eventField, gbc)

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel(texts.getString("game_details.date")), gbc)

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(dateField, gbc)

        gbc.insets = Insets(10, 10, 10, 10)
        gbc.fill = GridBagConstraints.NONE
        gbc.anchor = GridBagConstraints.CENTER

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.0
        gbc.gridwidth = 2

        val done = JButton(texts.getString("game_details.done"))
        done.addActionListener {
            setVisible(false)
            val details = GameDetails(
                    whiteField.text,
                    blackField.text,
                    eventField.text,
                    dateField.text)
            onGameDetailsEntered(details)
        }

        add(done, gbc)
    }

    fun initialiseDate(date: String) {
        if (date.isBlank()) {
            dateField.text = ""
            dateField.isEnabled = true
        } else {
            dateField.text = date
            dateField.isEnabled = false
        }
    }

    fun show(onGameDetailsEntered: (GameDetails) -> Unit) {
        this.onGameDetailsEntered = onGameDetailsEntered
        setLocationRelativeTo(parent)
        setVisible(true)
    }
}

class GameDetails(val white: String, val black: String, val event: String, val date: String) {

}