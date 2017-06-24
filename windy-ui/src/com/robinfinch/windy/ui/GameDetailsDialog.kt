package com.robinfinch.windy.ui

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class GameDetailsDialog(parent: JFrame) : JDialog(parent, "Game Details", true) {

    private val whiteField = JTextField()

    val white
        get() = whiteField.text

    private val blackField = JTextField()

    val black
        get() = blackField.text

    interface Listener {
        fun onGameDetailsEntered(dialog: GameDetailsDialog)
    }

    var listener = object : Listener {
        override fun onGameDetailsEntered(dialog: GameDetailsDialog) {}
    }

    init {
        layout = GridBagLayout()
        size = Dimension(300, 150)
        setLocationRelativeTo(parent)

        val gbc = GridBagConstraints()
        gbc.insets = Insets(10, 10, 0, 10)
        gbc.fill = GridBagConstraints.HORIZONTAL

        gbc.gridy = 0
        gbc.weighty = 1.0

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel("White"), gbc)

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(whiteField, gbc)

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel("Black"), gbc)

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

        val done = JButton("Done")
        done.addActionListener {
            listener.onGameDetailsEntered(this)
        }

        add(done, gbc)
    }
}