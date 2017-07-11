package com.robinfinch.windy.ui

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.text.format
import java.awt.Color
import java.awt.Component
import javax.swing.*
import javax.swing.border.EmptyBorder

class GameCellRenderer : JPanel(), ListCellRenderer<Game> {

    private val title: JLabel

    private val description: JLabel

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = EmptyBorder(6, 6, 6, 0)

        title = JLabel()
        title.setBold()
        add(title)

        description = JLabel()
        add(description)
    }

    override fun getListCellRendererComponent(list: JList<out Game>?, game: Game?,
                                              index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {

        if (game != null) {
            title.text = "${game.white} - ${game.black}   ${game.result.format()}"

            description.text = "${game.event}   ${game.date}"
        }

        if (isSelected) {
            background = Color.lightGray
        } else {
            background = Color.white
        }

        return this
    }
}