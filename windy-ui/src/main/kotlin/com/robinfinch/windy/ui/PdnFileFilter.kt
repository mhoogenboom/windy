package com.robinfinch.windy.ui

import java.io.File
import javax.swing.filechooser.FileFilter

object PdnFileFilter : FileFilter() {

    override fun accept(f: File) = f.name.endsWith(".pdn")

    override fun getDescription() = "PDN file"
}
