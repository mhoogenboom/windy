package com.robinfinch.windy.ui

import com.robinfinch.windy.core.game.AcceptDraw
import com.robinfinch.windy.core.game.ExecuteMove
import com.robinfinch.windy.core.game.Resign
import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.ui.board.Board
import com.robinfinch.windy.ui.controller.View
import com.robinfinch.windy.ui.controller.WindyController
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileFilter

fun main(args: Array<String>) {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeLater {
        WindyApp().start()
    }
}

class WindyApp : View, GameDetailsDialog.Listener, Board.Listener {

    private val frame: JFrame

    private val board: Board

    private val history: JTextArea

    private val acceptDraw: JButton
    private val resign: JButton

    private val proposeDrawField: JCheckBox

    private val controller: WindyController

    init {
        frame = JFrame()
        frame.title = "Windy"
        frame.layout = GridBagLayout()

        val gbc = GridBagConstraints()

        board = Board()
        board.style = Board.Style()
        board.listener = this

        gbc.gridy = 0
        gbc.gridheight = 3
        gbc.gridx = 0
        gbc.insets = Insets(0, 150, 0, 0)
        frame.add(board, gbc)

        history = JTextArea(0, 15)
        history.isEnabled = false
        history.lineWrap = true

        gbc.gridheight = 1
        gbc.weighty = 1.0
        gbc.gridx = 1
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(10, 10, 0, 10)
        frame.add(JScrollPane(history), gbc)

        acceptDraw = JButton("Accept Draw")

        gbc.gridy = 1
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        frame.add(acceptDraw, gbc)

        resign = JButton("Resign")

        gbc.gridy = 2
        frame.add(resign, gbc)

        proposeDrawField = JCheckBox("propose draw on next move")

        gbc.gridy = 3
        gbc.gridx = 0
        gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(10, 150, 10, 0)
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

    override fun setTitle(title: String) {
        frame.title = "Windy | ${title}"
    }

    override fun setBoard(position: Position, upsideDown: Boolean) {
        board.position = position
        board.upsideDown = upsideDown
    }

    override fun setHistory(moves: String) {
        history.text = moves
    }

    override fun enableAcceptDraw(enabled: Boolean) {
        acceptDraw.isEnabled = enabled
    }

    override fun onMoveEntered(moves: List<Move>): Boolean {
        val action = ExecuteMove(moves[0], proposeDrawField.isSelected) // todo
        proposeDrawField.isSelected = false
        return controller.onActionEntered(action)
    }

    override fun showMessage(message: String) {
        JOptionPane.showMessageDialog(frame, message)
    }

    override fun showSaveDialog(): File? {
        val fileChooser = JFileChooser()

        fileChooser.fileFilter = object : FileFilter() {

            override fun accept(f: File): Boolean {
                return f.name.endsWith(".pdn")
            }

            override fun getDescription(): String? {
                return "PDN file"
            }
        }

        val option = fileChooser.showSaveDialog(frame)

        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.selectedFile
        } else {
            return null
        }
    }

}
