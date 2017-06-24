package com.robinfinch.windy.ui

import com.robinfinch.windy.core.game.AcceptDraw
import com.robinfinch.windy.core.game.ExecuteMove
import com.robinfinch.windy.core.game.Resign
import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.ui.board.Board
import com.robinfinch.windy.ui.controller.View
import com.robinfinch.windy.ui.controller.WindyController
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

fun main(args: Array<String>) {

    SwingUtilities.invokeLater {
        WindyApp().start()
    }
}

class WindyApp : View, GameDetailsDialog.Listener, Board.Listener {

    private val frame: JFrame

    private val board: Board

    private val acceptDraw: JButton
    private val resign: JButton

    private val proposeDrawField: JCheckBox

    private val controller: WindyController

    init {
        board = Board()
        board.style = Board.Style()
        board.listener = this

        frame = JFrame()
        frame.title = "Windy App"
        frame.layout = GridBagLayout()

        val gbc = GridBagConstraints()

        gbc.gridy = 0
        gbc.gridheight = 3

        gbc.gridx = 0
        frame.add(board, gbc)

        acceptDraw = JButton("Accept Draw")

        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridheight = 1
        gbc.insets = Insets(10, 10, 0, 10)

        gbc.gridx = 1
        frame.add(acceptDraw, gbc)

        resign = JButton("Resign")

        gbc.gridy = 1
        frame.add(resign, gbc)

        proposeDrawField = JCheckBox("propose draw on next move")

        gbc.fill = GridBagConstraints.NONE
        gbc.gridy = 3
        gbc.gridx = 0
        gbc.insets = Insets(10, 10, 10, 10)
        frame.add(proposeDrawField, gbc)

        frame.pack()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        controller = WindyController(this)

        acceptDraw.addActionListener {
            controller.onActionEntered(AcceptDraw)
        }

        resign.addActionListener {
            controller.onActionEntered(Resign)
        }
    }

    fun start() {
        frame.setVisible(true)

        controller.onStart()
    }

    override fun enterGameDetails() {
        val dialog = GameDetailsDialog(frame)
        dialog.listener = this
        dialog.setVisible(true)
    }

    override fun onGameDetailsEntered(dialog: GameDetailsDialog) {
        dialog.setVisible(false)
        return controller.onGameDetailsEntered(dialog.white, dialog.black)
    }

    override fun enableAcceptDraw(enabled: Boolean) {
        acceptDraw.isEnabled = enabled
    }

    override fun setBoard(position: Position, upsideDown: Boolean) {
        board.position = position
        board.upsideDown = upsideDown
    }

    override fun onMoveEntered(moves: List<Move>): Boolean {
        val action = ExecuteMove(moves[0], proposeDrawField.isSelected) // todo
        proposeDrawField.isSelected = false
        return controller.onActionEntered(action)
    }

    override fun showMessage(message: String) {
        JOptionPane.showMessageDialog(frame, message)
    }
}
