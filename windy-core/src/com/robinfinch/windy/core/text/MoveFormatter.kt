package com.robinfinch.windy.core.text

import com.robinfinch.windy.core.position.Move

fun Move.format(): String {
    return if (steps.size == 0) {
        "${start}-${end}"
    } else {
        "${start}x${end}"
    }
}

fun Move.formatWithSteps(): String {
    return if (steps.size == 0) {
        "${start}-${end}"
    } else {
        val steps = steps.joinToString(separator = "x", transform = Int::toString)
        "${start}x${steps}x${end}"
    }
}