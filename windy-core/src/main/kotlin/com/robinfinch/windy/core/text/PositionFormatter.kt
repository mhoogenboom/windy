package com.robinfinch.windy.core.text

import com.robinfinch.windy.core.position.Position

fun Position.format(): String {

    val whitePosition = StringBuilder()
    val blackPosition = StringBuilder()

    for (square in 1..Position.NUMBER_OF_SQUARES) {
        if (!empty[square]) {
            with(if (white[square]) whitePosition else blackPosition) {
                append(' ')
                if (king[square]) {
                    append('K')
                }
                append(square)
            }
        }
    }

    return if (white[0]) "W: ${whitePosition} B: ${blackPosition}" else "B: ${blackPosition} W: ${whitePosition}"
}