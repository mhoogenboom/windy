package com.robinfinch.windy.core.board

import com.robinfinch.windy.core.position.Generator
import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.util.*
import javax.swing.JPanel


class Board : JPanel() {

    class Style(
            val squareSize: Int = 48,
            val colorLightSquare: Color = Color(224, 182, 103),
            val colorDarkSquare: Color = Color(198, 140, 31),
            val colorWhitePiece: Color = Color(255, 255, 255),
            val colorWhitePieceHit: Color = Color(128, 128, 128),
            val colorWhiteEdge: Color = Color(192, 192, 192),
            val colorBlackPiece: Color = Color(0, 0, 0),
            val colorBlackPieceHit: Color = Color(128, 128, 128),
            val colorBlackEdge: Color = Color(64, 64, 64)) {
    }

    var style = Style()
        set(style) {
            field = style
            preferredSize = Dimension(10 * style.squareSize, 11 * style.squareSize)
            repaint()
        }

    var upsideDown = false
        set(upsideDown) {
            field = upsideDown
            repaint()
        }

    var position = Position()
        set(position) {
            field = position
            handler.validMoves = Generator(position).generate()
            repaint()
        }

    fun enableSettingUp() {
        handler.enableSettingUp()
    }

    fun enableMoves(onMoveEntered: (List<Move>) -> Boolean) {
        handler.validMoves = Generator(position).generate()
        handler.enableMoves(onMoveEntered)
    }

    fun disableBoard() {
        handler.disable()
    }

    private class MouseHandler(private val board: Board) : MouseListener, MouseMotionListener {

        enum class Mode {
            SETTING_UP, MOVING, DISABLED
        }

        var mode: Mode = Mode.DISABLED

        fun enableSettingUp() {
            this.mode = Mode.SETTING_UP
            this.onMoveEntered = { false }
        }

        fun enableMoves(onMoveEntered: (List<Move>) -> Boolean) {
            this.mode = Mode.MOVING
            this.onMoveEntered = onMoveEntered
        }

        fun disable() {
            this.mode = Mode.DISABLED
            this.onMoveEntered = { false }
        }

        private var onMoveEntered: (List<Move>) -> Boolean = { false }

        var validMoves: List<Move> = emptyList()

        var cursorX: Int = 0
            private set

        var cursorY: Int = 0
            private set

        var cursorStart: Int = 0
            private set

        val cursorSteps = Stack<Int>()

        override fun mousePressed(e: MouseEvent?) {

            if (e != null) {
                cursorX = e.x
                cursorY = e.y
                startMove();
            }
        }

        override fun mouseDragged(e: MouseEvent?) {

            if ((e != null) && (cursorStart != 0)) {
                cursorX = e.x
                cursorY = e.y
                continueMove();
            }
        }

        override fun mouseReleased(e: MouseEvent?) {

            if ((e != null) && (cursorStart != 0)) {
                cursorX = e.x
                cursorY = e.y
                finishMove()
            }
        }

        override fun mouseEntered(e: MouseEvent?) {

        }

        override fun mouseExited(e: MouseEvent?) {

            if ((e != null) && (cursorStart != 0)) {
                cursorX = e.x
                cursorY = e.y
                finishMove()
            }
        }

        override fun mouseClicked(e: MouseEvent?) {

        }

        override fun mouseMoved(e: MouseEvent?) {

        }

        private fun startMove() {
            val square = board.squareNumberForCoordinates(cursorX, cursorY)

            val canStart = when (mode) {
                Mode.SETTING_UP -> (square < 0) || !board.position.empty[square]
                Mode.MOVING -> validMoves.any { it.start == square }
                Mode.DISABLED -> false
            }

            if (canStart) {
                cursorStart = square
                board.repaint()
            }
        }

        private fun continueMove() {
            val square = board.squareNumberForCoordinates(cursorX, cursorY)

            if (mode == Mode.MOVING) {
                cursorSteps.push(square)

                if (!validMoves.any { (it.start == cursorStart) && (it.steps startsWith cursorSteps) }) {
                    cursorSteps.pop()
                }
            }

            board.repaint();
        }

        private fun finishMove() {
            val cursorEnd = board.squareNumberForCoordinates(cursorX, cursorY)

            if (mode == Mode.SETTING_UP) {
                if (cursorStart < 0) {
                    if (cursorEnd > 0) {
                        board.position.empty[cursorEnd] = false
                        board.position.white[cursorEnd] = (cursorStart == -1) || (cursorStart == -2)
                        board.position.king[cursorEnd] = (cursorStart == -2) || (cursorStart == -4)
                    }
                } else {
                    if (cursorEnd != 0) {
                        board.position.empty[cursorStart] = true

                        if (cursorEnd > 0) {
                            board.position.empty[cursorEnd] = false
                            board.position.white[cursorEnd] = board.position.white[cursorStart]
                            board.position.king[cursorEnd] = board.position.king[cursorStart]
                        }
                    }
                }

                cursorStart = 0

                board.repaint()
            } else {
                val moves = validMoves.filter {
                    (it.start == cursorStart) && (it.steps startsWith cursorSteps) && (it.end == cursorEnd)
                }

                cursorSteps.clear()
                cursorStart = 0

                if (moves.isEmpty() || !onMoveEntered(moves)) {
                    board.repaint()
                }
            }
        }
    }

    private val handler = MouseHandler(this)

    init {
        preferredSize = Dimension(10 * style.squareSize, 11 * style.squareSize)

        addMouseListener(handler)
        addMouseMotionListener(handler)
    }

    override fun paintComponent(pad: Graphics) {

        val rh = RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON)
        (pad as Graphics2D).setRenderingHints(rh)

        pad.color = Color(0xf0, 0xf0, 0xf0)
        pad.fillRect(0, 0, 10 * style.squareSize, style.squareSize)

        for (col in (3..6)) {
            val squareNumber = squareNumber(-1, col)

            paintPiece(pad, squareNumber, col * style.squareSize, 0, false)
        }

        for (row in (0..9)) {
            val y = (row + 1) * style.squareSize;

            for (col in (0..9)) {
                val x = col * style.squareSize

                val squareNumber = squareNumber(row, col)

                if (squareNumber == 0) {
                    pad.color = style.colorLightSquare
                    pad.fillRect(x, y, style.squareSize, style.squareSize)
                } else {
                    pad.color = style.colorDarkSquare
                    pad.fillRect(x, y, style.squareSize, style.squareSize)

                    if (position.empty[squareNumber] || (squareNumber == handler.cursorStart)) {
                        // ignore
                    } else {
                        paintPiece(pad, squareNumber, x, y, squareNumber in handler.cursorSteps)
                    }
                }

                if (handler.cursorStart != 0) {
                    paintPiece(pad, handler.cursorStart, handler.cursorX - style.squareSize / 2, handler.cursorY - style.squareSize / 2, false)
                }
            }
        }
    }

    private fun paintPiece(pad: Graphics, squareNumber: Int, x: Int, y: Int, hit: Boolean) {

        if (squareNumber < 0) {
            val (colorPiece, colorEdge) =
                    if ((squareNumber == -1) || (squareNumber == -2)) {
                        Pair(style.colorWhitePiece, style.colorWhiteEdge)
                    } else {
                        Pair(style.colorBlackPiece, style.colorBlackEdge)
                    }

            if ((squareNumber == -2) || (squareNumber == -4)) {
                paintKing(pad, x, y, colorPiece, colorEdge)
            } else {
                paintMan(pad, x, y, colorPiece, colorEdge)
            }
        } else {
            val (colorPiece, colorEdge) =
                    if (position.white[squareNumber]) {
                        if (hit) {
                            Pair(style.colorWhitePieceHit, style.colorWhiteEdge)
                        } else {
                            Pair(style.colorWhitePiece, style.colorWhiteEdge)
                        }
                    } else {
                        if (hit) {
                            Pair(style.colorBlackPieceHit, style.colorBlackEdge)
                        } else {
                            Pair(style.colorBlackPiece, style.colorBlackEdge)
                        }
                    }

            if (position.king[squareNumber]) {
                paintKing(pad, x, y, colorPiece, colorEdge)
            } else {
                paintMan(pad, x, y, colorPiece, colorEdge)
            }
        }
    }

    private fun paintMan(pad: Graphics, x: Int, y: Int,
                         colorPiece: Color, colorEdge: Color) {

        // original equations:
        //        square size = s
        //        height of arcs = a = s / 4;
        //        height of piece = b = s / 4;
        //        width of piece = c = s * 2 / 3
        //
        //        g.setColor(colorPiece);
        //        g.fillArc(x + (s - c) / 2, y + (s - a - b) / 2, c, a, 0, 180);
        //        g.fillRect(x + (s - c) / 2, y + (s - b) / 2, c + 1, b + 1);
        //        g.fillArc(x + (s - c) / 2, y + (s - a + b) / 2, c, a, 180, 180);
        //
        //        g.setColor(colorEdge);
        //        g.drawArc(x + (s - c) / 2, y + (s - a - b) / 2, c, a, 0, 360);

        val squareSize6 = style.squareSize / 6
        val squareSize8 = style.squareSize / 8

        pad.color = colorPiece
        pad.fillArc(x + squareSize6, y + 2 * squareSize8, 4 * squareSize6, 2 * squareSize8, 0, 180)
        pad.fillRect(x + squareSize6, y + 3 * squareSize8, 4 * squareSize6 + 1, 2 * squareSize8 + 1)
        pad.fillArc(x + squareSize6, y + 4 * squareSize8, 4 * squareSize6, 2 * squareSize8, 180, 180)

        pad.color = colorEdge
        pad.drawArc(x + squareSize6, y + 2 * squareSize8, 4 * squareSize6, 2 * squareSize8, 0, 360)
    }

    private fun paintKing(pad: Graphics, x: Int, y: Int,
                          colorPiece: Color, colorEdge: Color) {

        // original equations
        //        square size = s
        //        height of arcs = a = s / 4;
        //        height of piece = b = s / 2;
        //        width of piece = c = s * 2 / 3;
        //
        //        g.setColor(colorPiece);
        //        g.fillArc(x + (s - c) / 2, y + (s - a - b) / 2, c, a, 0, 180);
        //        g.fillRect(x + (s - c) / 2, y + (s - b) / 2, c + 1, b + 1);
        //        g.fillArc(x + (s - c) / 2, y + (s - a + b) / 2, c, a, 180, 180);
        //
        //        g.setColor(colorEdge);
        //        g.drawArc(x + (s - c) / 2, y + (s - a - b) / 2, c, a, 0, 360);
        //        g.drawArc(x + (s - c) / 2, y + (s - a) / 2, c, a, 180, 180);

        val squareSize6 = style.squareSize / 6
        val squareSize8 = style.squareSize / 8

        pad.color = colorPiece
        pad.fillArc(x + squareSize6, y + squareSize8, 4 * squareSize6, 2 * squareSize8, 0, 180)
        pad.fillRect(x + squareSize6, y + 2 * squareSize8, 4 * squareSize6 + 1, 4 * squareSize8 + 1)
        pad.fillArc(x + squareSize6, y + 5 * squareSize8, 4 * squareSize6, 2 * squareSize8, 180, 180)

        pad.color = colorEdge
        pad.drawArc(x + squareSize6, y + squareSize8, 4 * squareSize6, 2 * squareSize8, 0, 360)
        pad.drawArc(x + squareSize6, y + 3 * squareSize8, 4 * squareSize6, 2 * squareSize8, 180, 180)
    }

    private fun squareNumberForCoordinates(x: Int, y: Int) =
            squareNumber(y / style.squareSize - 1, x / style.squareSize)

    private fun squareNumber(row: Int, col: Int) =
            if (row < 0)
                if ((col < 3) || (col > 6))
                    0
                else
                    2 - col
            else
                if ((col < 0) || (col > 9) || (row % 2 == col % 2))
                    0
                else
                    if (upsideDown)
                        50 - row * 5 - (col / 2)
                    else
                        1 + row * 5 + (col / 2)
}

infix fun IntArray.startsWith(head: Stack<Int>): Boolean {

    if (head.size > this.size) {
        return false;
    }
    for (i in 0..head.size - 1) {
        if (head[i] != this[i]) {
            return false
        }
    }
    return true;
}