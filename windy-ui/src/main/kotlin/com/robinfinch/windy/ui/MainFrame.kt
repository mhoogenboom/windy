package com.robinfinch.windy.ui

import com.robinfinch.windy.core.board.Board
import com.robinfinch.windy.core.game.*
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.storage.Query
import com.robinfinch.windy.ui.controller.View
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import java.util.*
import javax.swing.*


class MainFrame(private val texts: ResourceBundle) : View {

    private val frame: JFrame

    private val mainMenu: JMenu
    private val boardMenu: JMenu

    private val games: DefaultListModel<Game>
    private val gamesList: JList<Game>
    private val clearList: JButton

    private val board: Board
    private val proposeDrawField: JCheckBox

    private val history: JTextPane
    private val nextMove: JButton
    private val acceptDraw: JButton
    private val resign: JButton

    private val gameDetailsDialog: GameDetailsDialog
    private val searchGamesDialog: SearchGamesDialog

    init {
        mainMenu = JMenu(texts.getString("app.menu_main_title"))

        boardMenu = JMenu(texts.getString("app.menu_board_title"))

        val mb = JMenuBar()
        mb.add(mainMenu)
        mb.add(boardMenu)

        frame = JFrame()
        frame.title = texts.getString("app.title")
        frame.jMenuBar = mb
        frame.layout = GridBagLayout()

        val gbc = GridBagConstraints()

        games = DefaultListModel<Game>()

        gamesList = JList<Game>(games)
        gamesList.cellRenderer = GameCellRenderer()
        gamesList.selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION

        val listScroller = JScrollPane(gamesList)
        listScroller.preferredSize = Dimension(200, 0)

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridheight = 3
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(58, 10, 0, 10)
        frame.add(listScroller, gbc)

        clearList = JButton(texts.getString("controls.clear_list"))
        clearList.isEnabled = false

        gbc.gridy = 3
        gbc.gridheight = 1
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.insets = Insets(10, 10, 0, 10)
        frame.add(clearList, gbc)

        board = Board()
        board.style = Board.Style()

        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridheight = 4
        gbc.insets = Insets(10, 0, 0, 0)
        frame.add(board, gbc)

        proposeDrawField = JCheckBox(texts.getString("controls.propose_draw"))
        proposeDrawField.isEnabled = false

        val turnBoard = JMenuItem(texts.getString("app.menu_board_turn"))
        turnBoard.addActionListener { board.upsideDown = !board.upsideDown }
        boardMenu.add(turnBoard)

        gbc.gridy = 4
        gbc.gridheight = 1
        gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(10, 0, 10, 0)
        frame.add(proposeDrawField, gbc)

        history = JTextPane()
        history.preferredSize = Dimension(100, 0)
        history.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        history.contentType = "text/html"
        history.isEditable = false

        val historyScroller = JScrollPane(history)
        historyScroller.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

        gbc.gridx = 2
        gbc.gridy = 0
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(58, 10, 0, 10)
        frame.add(historyScroller, gbc)

        nextMove = JButton(texts.getString("controls.next_move"))
        nextMove.isEnabled = false

        gbc.gridy = 1
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.insets = Insets(10, 10, 0, 10)
        frame.add(nextMove, gbc)

        acceptDraw = JButton(texts.getString("controls.accept_draw"))
        acceptDraw.isEnabled = false

        gbc.gridy = 2
        frame.add(acceptDraw, gbc)

        resign = JButton(texts.getString("controls.resign"))
        resign.isEnabled = false

        gbc.gridy = 3
        frame.add(resign, gbc)

        frame.pack()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        gameDetailsDialog = GameDetailsDialog(frame, texts)

        searchGamesDialog = SearchGamesDialog(frame, texts)
    }

    override fun show(vararg plugins: JMenuItem) {

        for (plugin in plugins) {
            mainMenu.add(plugin)
        }

        frame.setVisible(true)
    }

    override fun enableMainMenu(enabled: Boolean) {
        mainMenu.isEnabled = enabled
    }

    override fun enableBoardMenu(enabled: Boolean) {
        boardMenu.isEnabled = enabled
    }

    override fun setGames(games: List<Game>) {
        this.games.clear()

        for (game in games) {
            this.games.addElement(game)
        }
    }

    override fun enableSelectGame(onGameSelected: ((ListSelection<Game>) -> Unit)?) {
        if (onGameSelected == null) {
            gamesList.disableWithoutSelectionListener()
            clearList.disableWithoutActionListener()
        } else {
            gamesList.enableWithSelectionListener { e ->
                if (!e.valueIsAdjusting && (gamesList.selectedValue != null)) {
                    onGameSelected(Selected(gamesList.selectedValue))
                }
            }
            clearList.enableWithActionListener {
                onGameSelected(Clear<Game>())
            }
        }
    }

    override fun setBoard(position: Position) {
        board.position = position
    }

    override fun setBoardUpsideDown(upsideDown: Boolean) {
        board.upsideDown = upsideDown
    }

    override fun getBoard() = board.position.copy()

    override fun setHistory(moves: String) {
        history.text = moves
        history.scrollToReference("current")
    }

    override fun enableNextMove(onNextMoveRequired: (() -> Unit)?) {

        if (onNextMoveRequired == null) {
            nextMove.disableWithoutActionListener()
        } else {
            nextMove.enableWithActionListener { onNextMoveRequired() }
        }
    }

    override fun enableSettingUpOnBoard() {
        board.enableSettingUp()
        proposeDrawField.isEnabled = false
    }

    override fun enableMovesOnBoard(onActionEntered: (com.robinfinch.windy.core.game.Action) -> Boolean) {

        board.enableMoves { moves ->
            val action = ExecuteMove(moves[0], proposeDrawField.isSelected) // todo
            proposeDrawField.isSelected = false
            onActionEntered(action)
        }
        proposeDrawField.isEnabled = true
    }

    override fun disableBoard() {
        board.disableBoard()
        proposeDrawField.isEnabled = false
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

    override fun enterGameDetails(onGameDetailsEntered: (Optional<GameDetails>) -> Unit,
                                  white: String, black: String, date: String) {

        gameDetailsDialog.initialise(white, black, date)
        gameDetailsDialog.show(onGameDetailsEntered)
    }

    override fun enterSearchCriteria(onSearchCriteriaEntered: (Optional<Query>) -> Unit) {

        searchGamesDialog.show(onSearchCriteriaEntered)
    }

    override fun showMessage(message: String) {
        JOptionPane.showMessageDialog(frame, message)
    }

    override fun showInputDialog(title: String, message: String): String {
        return JOptionPane.showInputDialog(frame, message, title, JOptionPane.QUESTION_MESSAGE);
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
