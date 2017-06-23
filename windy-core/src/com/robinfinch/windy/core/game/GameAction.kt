package com.robinfinch.windy.core.game

import com.robinfinch.windy.core.position.Move;

interface GameAction {

}

class ExecuteMove(val move: Move, val proposesDraw: Boolean) : GameAction {

}

object AcceptDraw : GameAction {

}

object Resign : GameAction {

}