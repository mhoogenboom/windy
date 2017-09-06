package com.robinfinch.windy.db

import com.robinfinch.windy.core.exercise.Exercise
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.storage.Query
import com.robinfinch.windy.core.storage.Storage
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.storage.Storable
import io.reactivex.Single
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

    override fun storeGames(games: List<Game>) =

            Single.fromCallable {
                for (game in games) {
                    store(game)

                    playerIndex.insert(game.white, game.black, id)

                    val position = Position()
                    position.start()

                    // skip the start position

                    for (move in game.moves()) {
                        position.execute(move)

                        positionIndex.insert(position, id)
                    }

                    positionIndex.flush()
                }
            }

    override fun findGamesByPlayer(query: Query): Single<List<Game>> =

            Single.fromCallable {

                val games = mutableListOf<Game>()

                val gamesPlayed = playerIndex.findFuzzy(query.player)
                if (query.withWhite) {
                    games.addAll(gamesPlayed.withWhite.map(this::load))
                }
                if (query.withBlack) {
                    games.addAll(gamesPlayed.withBlack.map(this::load))
                }

                games
            }

    override fun findGamesByPosition(position: Position): Single<List<Game>> =

            Single.fromCallable {
                val games: List<Game> = positionIndex.find(position).map(this::load)
                games
            }

    override fun storeExercise(exercise: Exercise) =

            Single.fromCallable {
                store(exercise)

                exerciseIndex.insert(id)

                positionIndex.insert(exercise.position, id)

                positionIndex.flush()
            }

    override fun countPass(exercise: Exercise) =

        Single.fromCallable {
            exerciseIndex.countPass(exercise.id)
        }

    override fun countFail(exercise: Exercise) =

            Single.fromCallable {
                exerciseIndex.countFail(exercise.id)
            }

    override fun findExercisesByScore(count: Int): Single<List<Exercise>> =

            Single.fromCallable {
                val exercises: List<Exercise> = exerciseIndex.find(count).map(this::load)
                exercises
            }

    override fun findExercisesByPosition(position: Position): Single<List<Exercise>> =

            Single.fromCallable {
                val exercises: List<Exercise> = positionIndex.find(position).map(this::load)
                exercises
            }

    private fun store(item: Storable) {

        id++

        item.id = id

        val file = fileFor(id)

        ObjectOutputStream(FileOutputStream(file)).use { oos ->
            oos.writeObject(item)
        }

        ObjectOutputStream(FileOutputStream(idFile)).use { oos ->
            oos.writeLong(id)
        }
    }

    private fun <T : Storable> load(id: Long): T {

        val file = fileFor(id)

        return ObjectInputStream(FileInputStream(file)).use { ois ->
            ois.readObject() as T
        }
    }

    private fun fileFor(id: Long) = File(dataDir, "${id}.ser")
}
