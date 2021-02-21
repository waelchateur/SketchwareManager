package io.sketchware.data

import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchwarepro.data.CustomComponent
import io.sketchware.utils.readFile
import io.sketchware.utils.serialize
import io.sketchware.utils.toJson
import io.sketchware.utils.writeFile
import java.io.File

class SketchwareProComponentsManager(private val file: File) {

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

    suspend fun getComponents(): List<CustomComponent> {
        return requireValue().serialize()
    }

    suspend fun addComponent(component: CustomComponent) =
        saveComponents(
            getComponents().toMutableList().apply {
                add(component)
            }
        )

    suspend fun removeComponent(component: CustomComponent) {
        saveComponents(getComponents().toMutableList().apply {
            remove(component)
        })
    }

    private suspend fun saveComponents(list: List<CustomComponent>) {
        file.writeFile(list.toJson().toByteArray())
    }

}