package com.robinfinch.windy.core.game

class Arbiter {

    private val game = Game()

    private var state = State.WAITING

    fun setWhite(name: String) {
        game.white = name
    }

    fun acceptWhite(action: GameAction): Boolean {

        if (state == State.WAITING) {
            game.start()
            state = State.IN_PROGRESS
        }

        if ((state == State.IN_PROGRESS) or (state == State.DRAW_PROPOSED)) {
            if (!game.whitesTurn()) {
                return false
            }
            when (action) {
                is ExecuteMove -> {
                    if (action.move in game.validMoves()) {
                        execute(action)
                        return true
                    }
                }
                is AcceptDraw -> {
                    if (state == State.DRAW_PROPOSED) {
                        finish(Game.Result.DRAW)
                        return true
                    }
                }
                is Resign -> {
                    finish(Game.Result.BLACK_WIN)
                    return true;
                }
                else -> return true
            }
        }

        return false
    }

    fun setBlack(name: String) {
        game.black = name
    }

    fun acceptBlack(action: GameAction): Boolean {

        if ((state == State.IN_PROGRESS) or (state == State.DRAW_PROPOSED)) {
            if (game.whitesTurn()) {
                return false
            }
            when (action) {
                is ExecuteMove -> {
                    if (action.move in game.validMoves()) {
                        execute(action)
                        return true
                    }
                }
                is AcceptDraw -> {
                    if (state == State.DRAW_PROPOSED) {
                        finish(Game.Result.DRAW)
                        return true
                    }
                }
                is Resign -> {
                    finish(Game.Result.WHITE_WIN)
                    return true;
                }
                else -> return true
            }
        }

        return false
    }

    private fun execute(action: ExecuteMove) {
        game.execute(action.move)
        state = if (action.proposesDraw) {
            State.DRAW_PROPOSED
        } else {
            State.IN_PROGRESS
        }
    }

    private fun finish(result: Game.Result) {
        game.result = result
        state = State.FINISHED
    }

    private enum class State {
        WAITING, IN_PROGRESS, DRAW_PROPOSED, FINISHED
    }
}