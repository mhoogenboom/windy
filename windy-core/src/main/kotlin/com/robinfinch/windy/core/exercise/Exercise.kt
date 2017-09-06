package com.robinfinch.windy.core.exercise

import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import com.robinfinch.windy.core.storage.Storable

class Exercise : Storable {

    override var id = 0L
    var position = Position()
    var challenge = ""
    var solution = mutableListOf<Move>()
}