package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.position.Position
import java.io.File

interface View {

    fun enterGameDetails()

    fun setTitle(title: String)

    fun setBoard(position: Position, upsideDown: Boolean)

    fun setHistory(moves: String)

    fun enableAcceptDraw(enabled: Boolean)

    fun showMessage(message: String)

    fun showSaveDialog(): File?
}