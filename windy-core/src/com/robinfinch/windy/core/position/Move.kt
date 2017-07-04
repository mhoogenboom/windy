package com.robinfinch.windy.core.position

import java.io.Serializable
import java.util.*

class Move(internal val start: Int, internal val steps: IntArray, internal val end: Int) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other is Move) {
            return (start == other.start) && (end == other.end) &&
                    Arrays.equals(steps, other.steps)
        } else {
            return false
        }
    }
}
