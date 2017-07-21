package com.robinfinch.windy.core.exercise

import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import java.io.Serializable

class Exercise : Serializable {

    var position = Position()
    var challenge = ""
    var solution = mutableListOf<Move>()
}