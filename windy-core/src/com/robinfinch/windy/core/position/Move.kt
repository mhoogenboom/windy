package com.robinfinch.windy.core.position

import java.util.*

class Move(val start: Int, val steps: IntArray, val end: Int) {

    override fun equals(other: Any?): Boolean {
        if (other is Move) {
            return (start == other.start) && (end == other.end) &&
                    Arrays.equals(steps, other.steps)
        } else {
            return false
        }
    }
}
