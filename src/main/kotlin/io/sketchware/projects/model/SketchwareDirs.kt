package io.sketchware.projects.model

import java.io.File

data class SketchwareDirs(
    val mysc: File?,
    val myscList: File,
    /** folder with info about view,logic */
    val bak: File?,
    /** folder with info about files, view, logic, resources, libraries */
    val data: File?,
    val resources: SketchwareResourcesPaths
)