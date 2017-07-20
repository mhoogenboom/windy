package com.robinfinch.windy.db

import com.robinfinch.windy.core.exercise.Exercise
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

    private val exerciseIndex: ExerciseIndex = ExerciseIndex(dataDir)

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

    override fun storeGame(game: Game) =

            Observable.fromCallable {
                store(game)

                playerIndex.insert(game.white, game.black, id)

                val position = Position()
                position.start()

                // skip the start position

                for (move in game.moves()) {
                    position.execute(move)

                    positionIndex.insert(position, id)
                }
            }

    override fun findGamesByPlayer(query: Query): Observable<List<Game>> =

            Observable.fromCallable {

                val games = mutableListOf<Game>()

                val gamesPlayed = playerIndex.find(query.player)
                if (query.withWhite) {
                    games.addAll(gamesPlayed.withWhite.map(this::load))
                }
                if (query.withBlack) {
                    games.addAll(gamesPlayed.withBlack.map(this::load))
                }

                games
            }

    override fun findGamesByPosition(position: Position): Observable<List<Game>> =

            Observable.fromCallable {
                val games: List<Game> = positionIndex.find(position).map(this::load)
                games
            }

    override fun storeExercise(exercise: Exercise) =

            Observable.fromCallable {
                store(exercise)

                exerciseIndex.insert(id)

                positionIndex.insert(exercise.position, id)
            }

    override fun findExercisesByScore(count: Int): Observable<List<Exercise>> =

            Observable.fromCallable {
                val exercises: List<Exercise> = exerciseIndex.find(count).map(this::load)
                exercises
            }

    override fun findExercisesByPosition(position: Position): Observable<List<Exercise>> =

            Observable.fromCallable {
                val exercises: List<Exercise> = positionIndex.find(position).map(this::load)
                exercises
            }

    private fun <T> store(item: T) {

        id++

        val file = fileFor(id)

        ObjectOutputStream(FileOutputStream(file)).use { oos ->
            oos.writeObject(item)
        }

        ObjectOutputStream(FileOutputStream(idFile)).use { oos ->
            oos.writeLong(id)
        }
    }

    private fun <T> load(id: Long): T {

        val file = fileFor(id)

        return ObjectInputStream(FileInputStream(file)).use { ois ->
            ois.readObject() as T
        }
    }

    private fun fileFor(id: Long) = File(dataDir, "${id}.ser")
}
