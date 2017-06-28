package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Move;

interface Action {

}

class ExecuteMove(val move: Move, val proposesDraw: Boolean) : Action {

}

object AcceptDraw : Action {

}

object Resign : Action {

}