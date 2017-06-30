package com.robinfinch.windy.db

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import java.io.*

class Database(private val dataDir: File) {

    private val idFile = File(dataDir, "id.ser")

    private var id: Long

    private val playerIndex: PlayerIndex = PlayerIndex(dataDir)

    private val positionIndex: PositionIndex = PositionIndex()

    init {
        id =
                if (idFile.exists()) {
                    ObjectInputStream(FileInputStream(idFile)).use { ois ->
                        ois.readLong()
                    }
                } else {
                    0
                }
    }

    fun saveGame(game: Game) {

        storeGame(game)

        playerIndex.insert(game.white, game.black, id)

        val position = Position()
        position.start()

        // skip the start position

        for (move in game.moves()) {
            position.execute(move)

            positionIndex.insert(position, id)
        }
    }

    fun findGamesByPlayer(name: String, includeWhite: Boolean = true, includeBlack: Boolean = true): List<Game> {

        val games = mutableListOf<Game>()

        val gamesPlayed = playerIndex.find(name)
        if (includeWhite) {
            games.addAll(gamesPlayed.withWhite.map(this::loadGame))
        }
        if (includeBlack) {
            games.addAll(gamesPlayed.withBlack.map(this::loadGame))
        }

        return games
    }

    fun findGamesByPosition(position: Position): List<Game> {

        return positionIndex.find(position).map(this::loadGame)
    }

    private fun loadGame(id: Long): Game {

        val file = fileFor(id)

        return ObjectInputStream(FileInputStream(file)).use { ois ->
            ois.readObject() as Game
        }
    }

    private fun storeGame(game: Game) {

        id++

        val file = fileFor(id)

        ObjectOutputStream(FileOutputStream(file)).use { oos ->
            oos.writeObject(game)
        }

        ObjectOutputStream(FileOutputStream(idFile)).use { oos ->
            oos.writeLong(id)
        }
    }

    private fun fileFor(id: Long) = File(dataDir, "${id}.ser")
}