package com.robinfinch.windy.ui

import com.robinfinch.windy.core.position.Generator
import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
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
            preferredSize = Dimension(10 * style.squareSize, 10 * style.squareSize)
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

    interface Listener {
        fun onMoveEntered(moves: List<Move>)
    }

    var listener = object : Listener {
        override fun onMoveEntered(moves: List<Move>) {}
    }

    private class MouseHandler(private val board: Board) : MouseListener, MouseMotionListener {

        var validMoves: List<Move> = emptyList()

        var moveX: Int = -1
            private set

        var moveY: Int = -1
            private set

        var moveStart: Int? = null
            private set

        val moveSteps = Stack<Int>()

        override fun mousePressed(e: MouseEvent?) {

            if (e != null) {
                moveX = e.x
                moveY = e.y
                startMove();
            }
        }

        override fun mouseDragged(e: MouseEvent?) {

            if ((e != null) && (moveStart != null)) {
                moveX = e.x
                moveY = e.y
                continueMove();
            }
        }

        override fun mouseReleased(e: MouseEvent?) {

            if ((e != null) && (moveStart != null)) {
                moveX = e.x
                moveY = e.y
                finishMove()
            }
        }

        override fun mouseEntered(e: MouseEvent?) {

        }

        override fun mouseExited(e: MouseEvent?) {

            if ((e != null) && (moveStart != null)) {
                moveX = e.x
                moveY = e.y
                finishMove()
            }
        }

        override fun mouseClicked(e: MouseEvent?) {

        }

        override fun mouseMoved(e: MouseEvent?) {

        }

        private fun startMove() {
            val square = board.squareNumberForCoordinates(moveX, moveY)

            if (validMoves.any { it.start == square }) {
                moveStart = square
            }

            board.repaint()
        }

        private fun continueMove() {
            val square = board.squareNumberForCoordinates(moveX, moveY)

            moveSteps.push(square)

            if (!validMoves.any { (it.start == moveStart) and (it.steps startsWith moveSteps) }) {
                moveSteps.pop()
            }

            board.repaint();
        }

        private fun finishMove() {
            val moveEnd = board.squareNumberForCoordinates(moveX, moveY)

            val moves = validMoves.filter {
                (it.start == moveStart) and (it.steps startsWith moveSteps) and (it.end == moveEnd)
            }

            moveSteps.clear()
            moveStart = null

            board.onMoveEntered(moves)
        }
    }

    private val handler = MouseHandler(this)

    init {
        preferredSize = Dimension(10 * style.squareSize, 10 * style.squareSize)

        addMouseListener(handler)
        addMouseMotionListener(handler)
    }

    private fun onMoveEntered(moves: List<Move>) {
        listener.onMoveEntered(moves)
    }

    override fun paintComponent(pad: Graphics) {

        for (row in (0..9)) {
            val y = row * style.squareSize;

            for (col in (0..9)) {
                val x = col * style.squareSize

                val squareNumber = squareNumber(row, col)

                if (squareNumber == 0) {
                    pad.color = style.colorLightSquare
                    pad.fillRect(x, y, style.squareSize, style.squareSize)
                } else {
                    pad.color = style.colorDarkSquare
                    pad.fillRect(x, y, style.squareSize, style.squareSize)

                    if (position.empty[squareNumber] || (squareNumber == handler.moveStart)) {
                        // ignore
                    } else {
                        paintPiece(pad, squareNumber, x, y, squareNumber in handler.moveSteps)
                    }
                }

                if (handler.moveStart != null) {
                    paintPiece(pad, handler.moveStart as Int, handler.moveX - style.squareSize / 2, handler.moveY - style.squareSize / 2, false)
                }
            }
        }
    }

    private fun paintPiece(pad: Graphics, squareNumber: Int, x: Int, y: Int, hit: Boolean) {

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
        pad.fillRect(x + squareSize6, y + 2 * squareSize8, 2 * squareSize6 + 1, 4 * squareSize8 + 1)
        pad.fillArc(x + squareSize6, y + 5 * squareSize8, 4 * squareSize6, 2 * squareSize8, 180, 180)

        pad.color = colorEdge
        pad.drawArc(x + squareSize6, y + squareSize8, 4 * squareSize6, 2 * squareSize8, 0, 360)
        pad.drawArc(x + squareSize6, y + 3 * squareSize8, 4 * squareSize6, 2 * squareSize8, 180, 180)
    }

    private fun squareNumberForCoordinates(x: Int, y: Int) =
            squareNumber(y / style.squareSize, x / style.squareSize)

    private fun squareNumber(row: Int, col: Int) =
            if (row % 2 == col % 2)
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