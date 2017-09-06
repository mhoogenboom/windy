package com.robinfinch.windy.core.storage

import com.robinfinch.windy.core.exercise.Exercise
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import io.reactivex.Single
import java.io.Serializable

interface Storage {

    fun storeGames(games: List<Game>): Single<Unit>

    fun findGamesByPlayer(query: Query): Single<List<Game>>

    fun findGamesByPosition(position: Position): Single<List<Game>>

    fun storeExercise(exercise: Exercise): Single<Unit>

    fun countPass(exercise: Exercise): Single<Unit>

    fun countFail(exercise: Exercise): Single<Unit>

    fun findExercisesByScore(count: Int): Single<List<Exercise>>

    fun findExercisesByPosition(position: Position): Single<List<Exercise>>
}

interface Storable : Serializable {

    var id: Long
}

class Query(val player: String, val withWhite: Boolean = true, val withBlack: Boolean = true, val position: Position) {

}

