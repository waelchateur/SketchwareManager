package io.sketchware.projects.model

import java.io.File

data class SketchwareResourcesPaths(
        val icons: File? = null,
        val images: File? = null,
        val fonts: File? = null,
        val sounds: File? = null
) {
    constructor(icons: String, images: String, fonts: String, sounds: String) :
            this(File(icons), File(images), File(fonts), File(sounds))
}

