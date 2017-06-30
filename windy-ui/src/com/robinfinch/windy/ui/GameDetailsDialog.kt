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

    var onGameDetailsEntered: (String, String) -> Unit = { white, black -> }

    init {
        layout = GridBagLayout()
        size = Dimension(300, 150)

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
            onGameDetailsEntered(whiteField.text, blackField.text)
        }

        add(done, gbc)
    }

    fun show(onGameDetailsEntered: (String, String) -> Unit) {
        this.onGameDetailsEntered = onGameDetailsEntered
        setLocationRelativeTo(parent)
        setVisible(true)
    }
}