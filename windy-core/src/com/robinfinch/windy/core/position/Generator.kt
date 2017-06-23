package com.robinfinch.windy.core.position

import java.util.*

class Generator(val position: Position) {

    companion object {

        val WHITE_FORWARD_LEFT = intArrayOf(0,
                0, 0, 0, 0, 0,
                0, 1, 2, 3, 4,
                6, 7, 8, 9, 10,
                0, 11, 12, 13, 14,
                16, 17, 18, 19, 20,
                0, 21, 22, 23, 24,
                26, 27, 28, 29, 30,
                0, 31, 32, 33, 34,
                36, 37, 38, 39, 40,
                0, 41, 42, 43, 44)

        val WHITE_FORWARD_RIGHT = intArrayOf(0,
                0, 0, 0, 0, 0,
                1, 2, 3, 4, 5,
                7, 8, 9, 10, 0,
                11, 12, 13, 14, 15,
                17, 18, 19, 20, 0,
                21, 22, 23, 24, 25,
                27, 28, 29, 30, 0,
                31, 32, 33, 34, 35,
                37, 38, 39, 40, 0,
                41, 42, 43, 44, 45)

        val BLACK_FORWARD_LEFT = intArrayOf(0,
                7, 8, 9, 10, 0,
                11, 12, 13, 14, 15,
                17, 18, 19, 20, 0,
                21, 22, 23, 24, 25,
                27, 28, 29, 30, 0,
                31, 32, 33, 34, 35,
                37, 38, 39, 40, 0,
                41, 42, 43, 44, 45,
                47, 48, 49, 50, 0,
                0, 0, 0, 0, 0)

        val BLACK_FORWARD_RIGHT = intArrayOf(0,
                6, 7, 8, 9, 10,
                0, 11, 12, 13, 14,
                16, 17, 18, 19, 20,
                0, 21, 22, 23, 24,
                26, 27, 28, 29, 30,
                0, 31, 32, 33, 34,
                36, 37, 38, 39, 40,
                0, 41, 42, 43, 44,
                46, 47, 48, 49, 50,
                0, 0, 0, 0, 0)
    }

    private val moves = mutableListOf<Move>()

    private var start = 0
    private var steps = Stack<Int>()
    private var maxStepsSize = 0
    private var end = 0

    fun generate(): List<Move> {

        if (position.white[0]) {
            generateForSquares(1.rangeTo(Position.NUMBER_OF_SQUARES),
                    WHITE_FORWARD_LEFT, WHITE_FORWARD_RIGHT, BLACK_FORWARD_LEFT, BLACK_FORWARD_RIGHT)
        } else {
            generateForSquares(Position.NUMBER_OF_SQUARES.downTo(1),
                    BLACK_FORWARD_LEFT, BLACK_FORWARD_RIGHT, WHITE_FORWARD_LEFT, WHITE_FORWARD_RIGHT)
        }

        return moves
    }

    private fun generateForSquares(squares: IntProgression,
                                   forwardLeft: IntArray, forwardRight: IntArray,
                                   backwardRight: IntArray, backwardLeft: IntArray) {

        for (square in squares) {
            if ((position.empty[square] == position.empty[0]) and (position.white[square] == position.white[0])) {
                start = square
                if (position.king[square]) {
                    generateForKing(forwardLeft, forwardRight, backwardRight, backwardLeft)
                } else {
                    generateForMan(forwardLeft, forwardRight, backwardRight, backwardLeft)
                }
            }
        }
    }

    private fun generateForMan(forwardLeft: IntArray, forwardRight: IntArray,
                               backwardRight: IntArray, backwardLeft: IntArray) {

        end = forwardLeft[start]
        if (position.empty[end]) {
            generateMove()
        } else if (position.white[end] != position.white[0]) {
            generateHitForMan(forwardLeft, backwardLeft, forwardRight, backwardRight)
        }

        end = forwardRight[start]
        if (position.empty[end]) {
            generateMove()
        } else if (position.white[end] != position.white[0]) {
            generateHitForMan(forwardRight, forwardLeft, backwardRight, backwardLeft)
        }

        end = backwardRight[start]
        if (!position.empty[end] and (position.white[end] != position.white[0])) {
            generateHitForMan(backwardRight, forwardRight, backwardLeft, forwardLeft)
        }

        end = backwardLeft[start]
        if (!position.empty[end] and (position.white[end] != position.white[0])) {
            generateHitForMan(backwardLeft, backwardRight, forwardLeft, forwardRight)
        }
    }

    private fun generateHitForMan(forward: IntArray, left: IntArray,
                                  right: IntArray, backward: IntArray) {

        val s = forward[end]

        if (position.empty[s]) {
            steps.push(end)
            position.white[end] = !position.white[end]

            end = left[s]
            if (!position.empty[end] and (position.white[end] != position.white[0])) {
                generateHitForMan(left, backward, forward, right)
            }

            end = forward[s]
            if (!position.empty[end] and (position.white[end] != position.white[0])) {
                generateHitForMan(forward, left, right, backward)
            }

            end = right[s]
            if (!position.empty[end] and (position.white[end] != position.white[0])) {
                generateHitForMan(right, forward, backward, left)
            }

            end = s
            generateMove()

            end = steps.pop()
            position.white[end] = !position.white[end]
        }
    }

    private fun generateForKing(forwardLeft: IntArray, forwardRight: IntArray,
                                backwardRight: IntArray, backwardLeft: IntArray) {

        end = forwardLeft[start]
        while (position.empty[end]) {
            generateMove()
            end = forwardLeft[end]
        }
        if (position.white[end] != position.white[0]) {
            generateHitForKing(forwardLeft, backwardLeft, forwardRight, backwardRight)
        }

        end = forwardRight[start]
        while (position.empty[end]) {
            generateMove()
            end = forwardRight[end]
        }
        if (position.white[end] != position.white[0]) {
            generateHitForKing(forwardRight, forwardLeft, backwardRight, backwardLeft)
        }

        end = backwardRight[start]
        while (position.empty[end]) {
            generateMove()
            end = backwardRight[end]
        }
        if (position.white[end] != position.white[0]) {
            generateHitForKing(backwardRight, forwardRight, backwardLeft, forwardLeft)
        }

        end = backwardLeft[start]
        while (position.empty[end]) {
            generateMove()
            end = backwardLeft[end]
        }
        if (position.white[end] != position.white[0]) {
            generateHitForKing(backwardLeft, backwardRight, forwardLeft, forwardRight)
        }
    }

    private fun generateHitForKing(forward: IntArray, left: IntArray,
                                   right: IntArray, backward: IntArray) {

        var s = forward[end]

        steps.push(end)
        position.white[end] = !position.white[end]

        while (position.empty[s]) {
            end = left[s]
            while (position.empty[end]) {
                end = left[end]
            }
            if (position.white[end] != position.white[0]) {
                generateHitForKing(left, backward, forward, right)
            }

            end = right[s]
            while (position.empty[end]) {
                end = right[end]
            }
            if (position.white[end] != position.white[0]) {
                generateHitForKing(right, forward, backward, left)
            }

            end = s
            generateMove()

            s = forward[end]
        }

        end = s
        if (position.white[end] != position.white[0]) {
            generateHitForKing(forward, left, right, backward)
        }

        end = steps.pop()
        position.white[end] = !position.white[end]
    }

    private fun generateMove() {
        if (steps.size > maxStepsSize) {
            moves.clear()
            maxStepsSize = steps.size
        }
        if (steps.size == maxStepsSize) {
            moves.add(Move(start, IntArray(steps.size, { steps[it] }), end))
        }
    }
}