package com.robinfinch.windy.ui

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.*

class GameDetailsDialog(parent: JFrame, texts: ResourceBundle)
    : JDialog(parent, texts.getString("game_details.title"), true) {

    private val whiteField = JTextField()

    private val blackField = JTextField()

    private val eventField = JTextField()

    private val dateField = JTextField()

    var onGameDetailsEntered: (Optional<GameDetails>) -> Unit = { _ -> }

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

        val done = JButton(texts.getString("game_details.done"))
        done.addActionListener {
            isVisible = false
            val details = GameDetails(
                    whiteField.text,
                    blackField.text,
                    eventField.text,
                    dateField.text)
            onGameDetailsEntered(Optional.of(details))
        }

        gbc.insets = Insets(10, 10, 10, 10)
        gbc.fill = GridBagConstraints.NONE
        gbc.anchor = GridBagConstraints.CENTER

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.0
        gbc.gridwidth = 2
        add(done, gbc)

        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                isVisible = false
                onGameDetailsEntered(Optional.empty())
            }
        })
    }

    fun initialise(white: String, black: String, date: String) {

        whiteField.text = white
        whiteField.isEnabled = white.isBlank()

        blackField.text = black
        blackField.isEnabled = black.isBlank()

        dateField.text = date
        dateField.isEnabled = date.isBlank()
    }

    fun show(onGameDetailsEntered: (Optional<GameDetails>) -> Unit) {
        this.onGameDetailsEntered = onGameDetailsEntered
        setLocationRelativeTo(parent)
        isVisible = true
    }
}

class GameDetails(val white: String, val black: String, val event: String, val date: String) {

}