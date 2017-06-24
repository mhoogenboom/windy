package com.robinfinch.windy.core.game

class Arbiter {

    private enum class State {
        SETTING_UP, IN_PROGRESS, DRAW_PROPOSED, FINISHED
    }

    private var state = State.FINISHED

    private lateinit var game: Game

    fun setupGame(): Boolean {
        if (state == State.FINISHED) {
            game = Game()
            game.start()
            state = State.SETTING_UP
            return true
        } else {
            return false
        }
    }

    var white
        get() = game.white
        set(name) {
            if (state == State.SETTING_UP) {
                game.white = name
            }
        }

    var black
        get() = game.black
        set(name) {
            if (state == State.SETTING_UP) {
                game.black = name
            }
        }

    val position
        get() = game.position()

    val drawProposed
        get() = (state == State.DRAW_PROPOSED)

    val result
        get() = game.result

    fun acceptWhite(action: GameAction): Boolean {

        if (state == State.SETTING_UP) {
            state = State.IN_PROGRESS
        }

        if ((state == State.IN_PROGRESS) || (state == State.DRAW_PROPOSED)) {
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

    fun acceptBlack(action: GameAction): Boolean {

        if ((state == State.IN_PROGRESS) || (state == State.DRAW_PROPOSED)) {
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
}