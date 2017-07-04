package com.robinfinch.windy.core.text

import com.robinfinch.windy.core.game.Game

fun Game.Result.format(): String {
    return when (this) {
        Game.Result.WHITE_WIN -> "2-0"
        Game.Result.DRAW -> "1-1"
        Game.Result.BLACK_WIN -> "0-2"
        Game.Result.UNKNOWN -> "?-?"
    }
}
