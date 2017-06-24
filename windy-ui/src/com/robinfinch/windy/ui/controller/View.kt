package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.core.position.Position

interface View {

    fun enterGameDetails()

    fun enableAcceptDraw(enabled: Boolean)

    fun setBoard(position: Position, upsideDown: Boolean)

    fun showMessage(message: String)
}