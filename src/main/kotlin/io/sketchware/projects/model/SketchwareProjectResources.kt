package io.sketchware.projects.model

import java.io.File

data class SketchwareProjectResources(
        /** List of font files */
        val fonts: List<File>?,
        /** List of image files */
        val images: List<File>?,
        /** List of sound files */
        val sounds: List<File>?,
        /** List of icon files */
        val icons: List<File>?
)