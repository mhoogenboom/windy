package com.robinfinch.windy.ui

import com.robinfinch.windy.core.board.Board
import com.robinfinch.windy.core.game.*
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.ui.controller.View
import java.awt.*
import java.awt.event.ActionEvent
import java.io.File
import java.text.MessageFormat
import java.util.*
import javax.swing.*
import javax.swing.DefaultListModel
import javax.swing.event.ListSelectionEvent

class MainFrame(private val texts: ResourceBundle) : View {

    private val frame: JFrame

    private val menu: JMenu

    private val gamesList: JList<Game>

    private val games: DefaultListModel<Game>

    private val board: Board

    private val history: JTextPane

    private val nextMove: JButton
    private val acceptDraw: JButton
    private val resign: JButton
    private val proposeDrawField: JCheckBox

    private val gameDetailsDialog: GameDetailsDialog

    private val searchGamesDialog: SearchGamesDialog

    init {
        frame = JFrame()
        frame.layout = GridBagLayout()

        menu = JMenu(texts.getString("app.menu_title"))

        val mb = JMenuBar()
        mb.add(menu)
        frame.jMenuBar = mb

        val gbc = GridBagConstraints()

        games = DefaultListModel<Game>()

        gamesList = JList<Game>(games)
        gamesList.selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION
        gamesList.isFocusable = false

        val listScroller = JScrollPane(gamesList)
        listScroller.preferredSize = Dimension(100, 0)

        gbc.gridy = 0
        gbc.gridheight = 4
        gbc.gridx = 0
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(10, 10, 0, 10)
        frame.add(listScroller, gbc)

        board = Board()
        board.style = Board.Style()

        gbc.gridx++
        gbc.insets = Insets(10, 0, 0, 0)
        frame.add(board, gbc)

        history = JTextPane()
        history.preferredSize = Dimension(100, 0)
        history.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        history.contentType = "text/html"
        history.isEditable = false

        val sp = JScrollPane(history)
        sp.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        gbc.gridheight = 1
        gbc.weighty = 1.0
        gbc.gridx++
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
        proposeDrawField.isEnabled = false

        gbc.gridy = 4
        gbc.gridx = 1
        gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(10, 0, 10, 0)
        frame.add(proposeDrawField, gbc)

        frame.pack()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        gameDetailsDialog = GameDetailsDialog(frame, texts)

        searchGamesDialog = SearchGamesDialog(frame, texts)
    }

    override fun show(vararg plugins: JMenuItem) {

        setTitle(texts.getString("app.welcome"))

        for (plugin in plugins) {
            menu.add(plugin)
        }

        frame.setVisible(true)
    }

    override fun setTitle(title: String) {
        frame.title = texts.getString("app.title", title)
    }

    override fun enableMenu(enabled: Boolean) {
        menu.isEnabled = enabled
    }

    override fun setGames(games: List<Game>) {
        for (game in games) {
            this.games.addElement(game)
        }
    }

    override fun enableSelectGame(onGameSelected: (Game) -> Unit) {
        gamesList.enableWithSelectionListener {e ->
            if (!e.valueIsAdjusting) {
                onGameSelected(gamesList.selectedValue)
            }
        }
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
            proposeDrawField.isEnabled = false
        } else {
            board.onMoveEntered = { moves ->
                val action = ExecuteMove(moves[0], proposeDrawField.isSelected) // todo
                proposeDrawField.isSelected = false
                onActionEntered(action)
            }
            proposeDrawField.isEnabled = true
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

    override fun enterGameDetails(date: String, onGameDetailsEntered: (GameDetails) -> Unit) {

        gameDetailsDialog.initialiseDate(date)
        gameDetailsDialog.show(onGameDetailsEntered)
    }

    override fun enterSearchCriteria(onSearchCriteriaEntered: (Query) -> Unit) {

        searchGamesDialog.show(onSearchCriteriaEntered)
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

fun <T> JList<T>.disableWithoutSelectionListener() {
    if (isFocusable) {
        isFocusable = false
        removeListSelectionListener(listSelectionListeners[0])
    }
}

fun <T> JList<T>.enableWithSelectionListener(listener: (ListSelectionEvent) -> Unit) {
    disableWithoutSelectionListener()

    addListSelectionListener(listener)
    isFocusable = true
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

fun ResourceBundle.getString(key: String, vararg params: Any): String =
        MessageFormat.format(getString(key), *params)
