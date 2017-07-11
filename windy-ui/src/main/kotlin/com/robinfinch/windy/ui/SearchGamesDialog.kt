package com.robinfinch.windy.ui

import com.robinfinch.windy.core.game.Query
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.*
import javax.swing.*

class SearchGamesDialog(parent: JFrame, texts: ResourceBundle)
    : JDialog(parent, texts.getString("search_games.title"), true) {

    private val playerField = JTextField()

    private val withWhiteField = JCheckBox()

    private val withBlackField = JCheckBox()

    var onSearchCriteriaEntered: (Query) -> Unit = { _ -> }

    init {
        layout = GridBagLayout()
        size = Dimension(300, 240)

        val gbc = GridBagConstraints()
        gbc.insets = Insets(10, 10, 0, 10)
        gbc.fill = GridBagConstraints.HORIZONTAL

        gbc.gridy = 0
        gbc.weighty = 1.0

        gbc.gridx = 0
        gbc.weightx = 0.3
        add(JLabel(texts.getString("search_games.player")), gbc)

        gbc.gridx = 1
        gbc.weightx = 0.7
        add(playerField, gbc)

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.3
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

        gbc.insets = Insets(10, 10, 10, 10)
        gbc.fill = GridBagConstraints.NONE
        gbc.anchor = GridBagConstraints.CENTER

        gbc.gridy++

        gbc.gridx = 0
        gbc.weightx = 0.0
        gbc.gridwidth = 2

        val done = JButton(texts.getString("search_games.done"))
        done.addActionListener {
            setVisible(false)
            onSearchCriteriaEntered(Query(playerField.text, withWhiteField.isSelected, withBlackField.isSelected))
        }

        add(done, gbc)
    }

    fun show(onSearchCriteriaEntered: (Query) -> Unit) {
        this.onSearchCriteriaEntered = onSearchCriteriaEntered
        setLocationRelativeTo(parent)
        setVisible(true)
    }
}
