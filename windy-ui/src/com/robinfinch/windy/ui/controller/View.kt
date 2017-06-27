package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.position.Position
import java.io.File

interface View {

    fun enterGameDetails()

    fun enableAcceptDraw(enabled: Boolean)

    fun setBoard(position: Position, upsideDown: Boolean)

    fun showMessage(message: String)

    fun showSaveDialog(): File?
}