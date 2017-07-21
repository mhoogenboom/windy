package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.exercise.Exercise
import com.robinfinch.windy.core.game.Action
import com.robinfinch.windy.core.game.ExecuteMove
import com.robinfinch.windy.core.game.Resign
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.text.format
import com.robinfinch.windy.db.Database
import com.robinfinch.windy.ui.edt
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.swing.JMenuItem

class InputExerciseController(private val view: View, private val texts: ResourceBundle, private val db: Database) {

    lateinit var exercise: Exercise
    lateinit var position: Position

    fun attachToMenu(): JMenuItem {
        val menuItem = JMenuItem(texts.getString("input_exercise.menu"))
        menuItem.addActionListener {
            view.enableMenu(false)

            exercise = Exercise()
            setUp()
        }
        return menuItem
    }

    private fun setUp() {

        view.enableSettingUpOnBoard();
        view.enableNextMove {
            view.disableBoard()
            view.enableNextMove(null)

            exercise.position = view.getBoard()
            enterChallenge()
        }
    }

    private fun enterChallenge() {

        val challenge = view.showInputDialog(texts.getString("input_exercise.challenge_title"), texts.getString("input_exercise.challenge_message"))

        if (challenge == null) {
            view.enableMenu(true)
        } else {
            exercise.challenge = challenge
            enterSolution()
        }
    }

    private fun enterSolution() {

        position = exercise.position.copy()

        view.enableMovesOnBoard(this::play)
        view.enableResign(this::play)
    }

    private fun play(action: Action) =

            when {
                action is ExecuteMove -> {
                    exercise.solution.add(action.move)
                    position.execute(action.move)
                    view.setBoard(position)
                    view.setHistory(exercise.solution.format(html = true))
                    true
                }
                action is Resign -> {
                    onExerciseEntered(exercise)
                    true
                }
                else -> {
                    false
                }
            }

    private fun onExerciseEntered(exercise: Exercise) {

        db.storeExercise(exercise)
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe {
                    view.disableBoard()
                    view.setBoard(Position())
                    view.setHistory("")
                    view.enableMenu(true)
                }
    }
}