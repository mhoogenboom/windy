package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.exercise.Exercise
import com.robinfinch.windy.core.position.Position
import io.reactivex.Observable

interface Storage {

    fun storeGames(games: List<Game>): Observable<Unit>

    fun findGamesByPlayer(query: Query): Observable<List<Game>>

    fun findGamesByPosition(position: Position): Observable<List<Game>>

    fun storeExercise(exercise: Exercise): Observable<Unit>

    fun findExercisesByScore(count: Int): Observable<List<Exercise>>

    fun findExercisesByPosition(position: Position): Observable<List<Exercise>>
}

class Query(val player: String, val withWhite: Boolean = true, val withBlack: Boolean = true, val position: Position) {

}