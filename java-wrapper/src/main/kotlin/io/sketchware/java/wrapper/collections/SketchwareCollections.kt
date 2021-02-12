package io.sketchware.java.wrapper.collections

import io.sketchware.collections.CollectionsManager
import java.io.File

class SketchwareCollections(private val file: File) {
    private val manager = CollectionsManager(file)

    fun getMoreblocksManager() = SketchwareCollection(File(file, "more_block/list"))
    fun getImagesManager() = SketchwareCollection(File(file, "image/list"))
    fun getFontsManager() = SketchwareCollection(File(file, "font/list"))
    fun getWidgetsManager() = SketchwareCollection(File(file, "widget/list"))
    fun getBlocksManager() = SketchwareCollection(File(file, "block/list"))
    fun getSoundsManager() = SketchwareCollection(File(file, "sound/list"))

}