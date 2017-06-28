package com.robinfinch.windy.ui

import com.robinfinch.windy.core.board.Board
import com.robinfinch.windy.core.game.AcceptDraw
import com.robinfinch.windy.core.game.ExecuteMove
import com.robinfinch.windy.core.game.Resign
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.ui.controller.View
import com.robinfinch.windy.ui.controller.WindyController
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.io.File
import java.text.MessageFormat
import java.util.*
import javax.swing.*

fun main(args: Array<String>) {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeLater {
        WindyApp().start()
    }
}

class WindyApp : View, GameDetailsDialog.Listener {

    private val frame: JFrame

    private val board: Board

    private val history: JTextArea

    private val nextMove: JButton
    private val acceptDraw: JButton
    private val resign: JButton

    private val proposeDrawField: JCheckBox

    private val controller: WindyController

    private val texts = ResourceBundle.getBundle("com.robinfinch.windy.ui.texts")

    init {
        frame = JFrame()
        frame.layout = GridBagLayout()

        val gbc = GridBagConstraints()

        board = Board()
        board.style = Board.Style()

        gbc.gridy = 0
        gbc.gridheight = 4
        gbc.gridx = 0
        gbc.insets = Insets(0, 150, 0, 0)
        frame.add(board, gbc)

        history = JTextArea(0, 15)
        history.isEnabled = false
        history.lineWrap = true

        val sp = JScrollPane(history)
        sp.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        gbc.gridheight = 1
        gbc.weighty = 1.0
        gbc.gridx = 1
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(10, 10, 0, 10)
        frame.add(sp, gbc)

        nextMove = JButton(texts.getString("controls.next_move"))
        nextMove.isEnabled = false

        gbc.gridy = 1
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        frame.add(nextMove, gbc)

        acceptDraw = JButton(texts.getString("controls.accept_draw"))
        acceptDraw.isEnabled = false

        gbc.gridy = 2
        frame.add(acceptDraw, gbc)

        resign = JButton(texts.getString("controls.resign"))
        resign.isEnabled = false

        gbc.gridy = 3
        frame.add(resign, gbc)

        proposeDrawField = JCheckBox(texts.getString("controls.propose_draw"))

        gbc.gridy = 4
        gbc.gridx = 0
        gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(10, 150, 10, 0)
        frame.add(proposeDrawField, gbc)

        frame.pack()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        controller = WindyController(this, texts)
    }

    fun start() {
        frame.setVisible(true)

        controller.onStart()
    }

    override fun enterGameDetails() {
        val dialog = GameDetailsDialog(frame, texts)
        dialog.listener = this
        dialog.setVisible(true)
    }

    override fun onGameDetailsEntered(dialog: GameDetailsDialog) {
        dialog.setVisible(false)
        controller.onGameDetailsEntered(dialog.white, dialog.black)
    }

    override fun setTitle(title: String) {
        frame.title = texts.getStrng("app.title", title)
    }

    override fun setBoard(position: Position, upsideDown: Boolean) {
        board.position = position
        board.upsideDown = upsideDown
    }

    override fun setHistory(moves: String) {
        history.text = moves
    }

    override fun enableNextMove(onNextMoveRequired: (() -> Unit)?) {

        if (onNextMoveRequired == null) {
            nextMove.disableWithoutActionListener()
        } else {
            nextMove.enableWithActionListener { onNextMoveRequired() }
        }
    }

    override fun enableMovesOnBoard(onActionEntered: ((com.robinfinch.windy.core.game.Action) -> Boolean)?) {

        if (onActionEntered == null) {
            board.onMoveEntered = { false }
        } else {
            board.onMoveEntered = { moves ->
                val action = ExecuteMove(moves[0], proposeDrawField.isSelected) // todo
                proposeDrawField.isSelected = false
                onActionEntered(action)
            }
        }
    }

    override fun enableAcceptDraw(onActionEntered: ((com.robinfinch.windy.core.game.Action) -> Boolean)?) {

        if (onActionEntered == null) {
            acceptDraw.disableWithoutActionListener()
        } else {
            acceptDraw.enableWithActionListener { onActionEntered(AcceptDraw) }
        }
    }

    override fun enableResign(onActionEntered: ((com.robinfinch.windy.core.game.Action) -> Boolean)?) {

        if (onActionEntered == null) {
            resign.disableWithoutActionListener()
        } else {
            resign.enableWithActionListener { onActionEntered(Resign) }
        }
    }

    override fun showMessage(message: String) {
        JOptionPane.showMessageDialog(frame, message)
    }

    override fun showOpenDialog(): File? {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = PdnFileFilter

        val option = fileChooser.showOpenDialog(frame)

        return if (option == JFileChooser.APPROVE_OPTION)
            fileChooser.selectedFile
        else
            null
    }

    override fun showSaveDialog(): File? {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = PdnFileFilter

        val option = fileChooser.showSaveDialog(frame)

        return if (option == JFileChooser.APPROVE_OPTION)
            fileChooser.selectedFile
        else
            null
    }
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

fun ResourceBundle.getStrng(key: String, vararg params: String): String =
    MessageFormat.format(getString(key), *params)
