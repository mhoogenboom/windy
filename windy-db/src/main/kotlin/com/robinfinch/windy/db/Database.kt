package com.robinfinch.windy.db

import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.game.Query
import com.robinfinch.windy.core.game.Storage
import com.robinfinch.windy.core.position.Position
import io.reactivex.Observable
import java.io.*

class Database(private val dataDir: File) : Storage {

    private val idFile = File(dataDir, "id.ser")

    private var id: Long

    private val playerIndex: PlayerIndex = PlayerIndex(dataDir)

    private val positionIndex: PositionIndex = PositionIndex(dataDir)

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

    override fun store(game: Game) =

            Observable.fromCallable {
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

    override fun findByPlayer(query: Query): Observable<List<Game>> =

            Observable.fromCallable {

                val games = mutableListOf<Game>()

                val gamesPlayed = playerIndex.find(query.player)
                if (query.withWhite) {
                    games.addAll(gamesPlayed.withWhite.map(this::loadGame))
                }
                if (query.withBlack) {
                    games.addAll(gamesPlayed.withBlack.map(this::loadGame))
                }

                games
            }

    override fun findByPosition(position: Position): Observable<List<Game>> =

            Observable.fromCallable {
                positionIndex.find(position).map(this::loadGame)
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
