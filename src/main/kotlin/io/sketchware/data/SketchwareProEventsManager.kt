package io.sketchware.data

import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchwarepro.data.CustomEvent
import io.sketchware.utils.readFile
import io.sketchware.utils.serialize
import io.sketchware.utils.toJson
import io.sketchware.utils.writeFile
import java.io.File

class SketchwareProEventsManager(private val file: File) {
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

    suspend fun getEvents() =
        requireValue().serialize<List<CustomEvent>>()

    suspend fun addEvent(event: CustomEvent) {
        saveEvents(getEvents().toMutableList().apply {
            add(event)
        })
    }

    suspend fun removeEvent(event: CustomEvent) {
        saveEvents(getEvents().toMutableList().apply {
            remove(event)
        })
    }

    private suspend fun saveEvents(events: List<CustomEvent>) {
        file.writeFile(events.toJson().toByteArray())
    }

}