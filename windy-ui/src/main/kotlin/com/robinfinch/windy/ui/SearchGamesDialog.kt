package com.robinfinch.windy.ui

import com.robinfinch.windy.core.board.Board
import com.robinfinch.windy.core.game.Query
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.*

class SearchGamesDialog(parent: JFrame, texts: ResourceBundle)
    : JDialog(parent, texts.getString("search_games.title"), true) {

    private val playerField = JTextField()

    private val withWhiteField = JCheckBox()

    private val withBlackField = JCheckBox()

    private val board = Board()

    var onSearchCriteriaEntered: (Optional<Query>) -> Unit = { _ -> }

    init {
        layout = GridBagLayout()

        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL

        gbc.gridy = 0

        gbc.gridx = 0
        gbc.weightx = 0.3
        gbc.insets = Insets(34, 10, 0, 0)
        add(JLabel(texts.getString("search_games.player")), gbc)

        playerField.columns = 20

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(playerField, gbc)

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.3
        gbc.insets = Insets(10, 10, 0, 0)
        add(JLabel(texts.getString("search_games.with_white")), gbc)

        withWhiteField.isSelected = true

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(withWhiteField, gbc)

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel(texts.getString("search_games.with_black")), gbc)

        withBlackField.isSelected = true

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(withBlackField, gbc)

        val done = JButton(texts.getString("search_games.done"))
        done.addActionListener {
            isVisible = false
            val query = Query(
                    playerField.text,
                    withWhiteField.isSelected,
                    withBlackField.isSelected,
                    board.position)
            onSearchCriteriaEntered(Optional.of(query))
        }

        gbc.insets = Insets(10, 0, 10, 0)
        gbc.fill = GridBagConstraints.NONE
        gbc.anchor = GridBagConstraints.CENTER

        gbc.gridy = 4

        gbc.gridx = 0
        gbc.weightx = 0.0
        gbc.gridwidth = 3
        add(done, gbc)

        board.style = Board.Style(squareSize = 24)
        board.enableSettingUp()

        gbc.gridx = 2
        gbc.gridwidth = 1
        gbc.gridy = 0
        gbc.gridheight = 4
        gbc.insets = Insets(10, 10, 0, 10)
        add(board, gbc)

        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                isVisible = false
                onSearchCriteriaEntered(Optional.empty())
            }
        })

        pack()
    }

    fun show(onSearchCriteriaEntered: (Optional<Query>) -> Unit) {
        this.onSearchCriteriaEntered = onSearchCriteriaEntered
        setLocationRelativeTo(parent)
        isVisible = true
    }
}
