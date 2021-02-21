package io.sketchware.data

import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchwarepro.data.CustomListener
import io.sketchware.utils.readFile
import io.sketchware.utils.serialize
import io.sketchware.utils.toJson
import io.sketchware.utils.writeFile
import java.io.File

class SketchwareProListenersManager(private val file: File) {
    private var value: String? = null

    init {
        if (file.isDirectory)
            throw SketchwareFileError(file.path)
        if (!file.exists())
            value = "[]"
    }

    private suspend fun requireValue(): String {
        if (value == null)
            value = file.readFile().serialize()
        return value ?: error("value shouldn't be null.")
    }

    suspend fun getListeners() =
        requireValue().serialize<List<CustomListener>>()

    suspend fun addEvent(listener: CustomListener) {
        saveListeners(getListeners().toMutableList().apply {
            add(listener)
        })
    }

    suspend fun removeEvent(listener: CustomListener) {
        saveListeners(getListeners().toMutableList().apply {
            remove(listener)
        })
    }

    private suspend fun saveListeners(events: MutableList<CustomListener>) {
        file.writeFile(events.toJson().toByteArray())
    }

}