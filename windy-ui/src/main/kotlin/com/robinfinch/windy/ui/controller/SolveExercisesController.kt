package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.exercise.Exercise
import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.game.Arbiter
import com.robinfinch.windy.core.game.ExecuteMove
import com.robinfinch.windy.core.game.Game
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.format
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.edt
import com.robinfinch.windy.ui.getString
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.swing.JMenuItem

class SolvesExercisesController(private val view: View, private val texts: ResourceBundle, private val db: Database) {

    private var exercises = emptyList<Exercise>()
    private var currentExercise = 0
    private var currentMove = 0

    private val arbiter: Arbiter = Arbiter()

    private var solveForWhite: Boolean = true

    fun attachToMenu(): JMenuItem {
        val menuItem = JMenuItem(texts.getString("solve_exercises.menu"))
        menuItem.addActionListener { start() }
        return menuItem
    }

    private fun start() {
        view.enableMainMenu(false)

        db.findExercisesByScore(12)
            .subscribeOn(Schedulers.io())
            .observeOn(edt())
            .subscribe(this::solveExercises)
    }

    private fun solveExercises(exercises: List<Exercise>) {
        this.exercises = exercises
        this.currentExercise = 0
        solveExercise()
    }

    private fun solveExercise() {

        val exercise = exercises[currentExercise]

        arbiter.setUpExercise(exercise.position)

        solveForWhite = arbiter.whiteToPlay

        view.setBoard(arbiter.currentPosition)
        view.setBoardUpsideDown(!solveForWhite)

        currentMove = 0

        view.enableMovesOnBoard(this::onActionEntered)
    }

    private fun onActionEntered(action: Action): Boolean {

        if (solveForWhite) {
            if (arbiter.acceptWhite(action)) {
                onActionAccepted(action)
                return true
            } else {
                return false
            }
        } else {
            if (arbiter.acceptBlack(action)) {
                onActionAccepted(action)
                return true
            } else {
                return false
            }
        }
    }

    private fun onActionAccepted(action: Action) {
        view.disableBoard()

        val exercise = exercises[currentExercise]

        if ((action is ExecuteMove) && (action.move == exercise.solution[currentMove] )) {
            view.setBoard(arbiter.currentPosition)
            view.setHistory(arbiter.currentGame.moves().format(html = true))

            currentMove++

            if (currentMove < exercise.solution.size) {
                val counterAction = ExecuteMove(exercise.solution[currentMove], false)

                if (solveForWhite) {
                    if (arbiter.acceptBlack(counterAction)) {
                        onActionCountered()
                    }
                } else {
                    if (arbiter.acceptBlack(counterAction)) {
                        onActionCountered()
                    }
                }
            } else {
                onExerciseSolved()
            }
        } else {
            onExerciseNotSolved()
        }
    }

    private fun onActionCountered() {

        currentMove++

        view.enableMovesOnBoard(this::onActionEntered)
    }

    private fun onExerciseSolved() {

        db.countPass(exercises[currentExercise])
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe(solveNextExercise)
    }

    private fun onExerciseNotSolved() {

        db.countFail(exercises[currentExercise])
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe(solveNextExercise)
    }

    private fun finish(message: String) {

        view.disableBoard()
        view.enableAcceptDraw(null)
        view.enableResign(null)

        view.showMessage(message)

        arbiter.saveGame(db)
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe {
                    view.setGames(emptyList())
                    view.setBoard(Position())
                    view.setHistory("")
                    view.enableMainMenu(true)
                }
    }
}