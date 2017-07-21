package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Position

class Arbiter {

    private enum class State {
        SETTING_UP, IN_PROGRESS, DRAW_PROPOSED, FINISHED
    }

    private var state = State.FINISHED

    private lateinit var game: Game

    private lateinit var position: Position

    fun setupGame(): Boolean {
        if (state == State.FINISHED) {
            position = Position()
            position.start()
            game = Game()
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

    var event
        get() = game.event
        set(event) {
            if (state == State.SETTING_UP) {
                game.event = event
            }
        }

    var date
        get() = game.date
        set(date) {
            if (state == State.SETTING_UP) {
                game.date = date
            }
        }

    val currentPosition
        get() = position.copy()

    val currentGame
        get() = game.copy()

    val drawProposed
        get() = (state == State.DRAW_PROPOSED)

    fun acceptWhite(action: Action): Boolean {

        if (state == State.SETTING_UP) {
            state = State.IN_PROGRESS
        }

        if ((state == State.IN_PROGRESS) || (state == State.DRAW_PROPOSED)) {
            if (!position.white[0]) {
                return false
            }
            when (action) {
                is ExecuteMove -> {
                    if (action.move in position.validMoves()) {
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

    fun acceptBlack(action: Action): Boolean {

        if ((state == State.IN_PROGRESS) || (state == State.DRAW_PROPOSED)) {
            if (position.white[0]) {
                return false
            }
            when (action) {
                is ExecuteMove -> {
                    if (action.move in position.validMoves()) {
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
        position.execute(action.move)
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

    fun saveGame(storage: Storage) = storage.storeGames(listOf(game))
}