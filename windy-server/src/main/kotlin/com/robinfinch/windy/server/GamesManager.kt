package com.robinfinch.windy.server

import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.game.Arbiter
import com.robinfinch.windy.core.game.RemoteGameStatus
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class GamesManager {

    private val arbiters = HashMap<String, Arbiter>()

    fun connectWhiteToGame(name: String, gameId: String) {

        val arbiter = arbiters.getOrPut(gameId, this::newArbiter)

        if (arbiter.white.isBlank()) {
            arbiter.white = name
        } else {
            throw InvalidGameStateException("White already connected to game ${gameId}")
        }
    }

    fun connectBlackToGame(name: String, gameId: String) {

        val arbiter = arbiters.getOrPut(gameId, this::newArbiter)

        if (arbiter.black.isBlank()) {
            arbiter.black = name
        } else {
            throw InvalidGameStateException("Black already connected to game ${gameId}")
        }
    }

    fun getStatus(gameId: String) : RemoteGameStatus {

        val arbiter = arbiters.getOrElse(gameId, { throw GameNotFoundException(gameId) })

        val status = RemoteGameStatus()
        status.white = arbiter.white
        status.black = arbiter.black
        status.status = ""
        return status
    }

    private fun newArbiter(): Arbiter {
        val arbiter = Arbiter()
        arbiter.setupGame()
        return arbiter
    }

    fun act(name: String, gameId: String, action: Action) {

        val arbiter = arbiters.getOrElse(gameId, { throw GameNotFoundException(gameId) })

        if (!arbiter.accept(name, action)) {
            throw InvalidActionException("Can't do that in game ${gameId}")
        }
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class GameNotFoundException(id: String) : RuntimeException("Game ${id} not found")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidGameStateException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidActionException(message: String) : RuntimeException(message)