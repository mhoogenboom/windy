package com.robinfinch.windy.core.text

import com.robinfinch.windy.core.position.Move
import com.robinfinch.windy.core.position.Position
import java.io.BufferedWriter
import java.io.StringWriter

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

fun List<Move>.format(plyPerLine: Int = 2,
                      writeDuplicatesInLongForm: Boolean = false,
                      html: Boolean = false,
                      emphasize: Int = -1): String {

    val history = StringWriter()
    BufferedWriter(history).use { out -> this.formatTo(out, plyPerLine, writeDuplicatesInLongForm, html, emphasize) }
    return history.toString()
}

fun List<Move>.formatTo(out: BufferedWriter,
                      plyPerLine: Int = 2,
                      writeDuplicatesInLongForm: Boolean = false,
                      html: Boolean = false,
                      emphasize: Int = -1) {

    val position = Position()
    position.start()

    var ply = 0

    for (move in this) {
        if (ply % 2 == 0) {
            if ((ply % plyPerLine == 0) && (ply > 0)) {
                if (html) {
                    out.write("<br/>")
                } else {
                    out.newLine()
                }
            }
            out.write("${1 + ply / 2}.")
        }

        val shortForm = move.format()

        val duplicates =
                if (writeDuplicatesInLongForm)
                    position.validMoves().map(Move::format).count { it == shortForm }
                else
                    1

        if (html && (ply == emphasize)) {
            out.write("<a name=\"current\"/><b>")
        }
        out.write(if (duplicates == 1) shortForm else move.formatWithSteps())
        if (html && (ply == emphasize)) {
            out.write("</b>")
        }
        out.write(" ")

        position.execute(move)
        ply++
    }
}