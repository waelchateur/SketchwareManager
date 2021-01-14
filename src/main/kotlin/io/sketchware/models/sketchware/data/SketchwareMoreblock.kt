package io.sketchware.models.sketchware.data

import kotlinx.serialization.Serializable

@Serializable
data class SketchwareMoreblock(
    val name: String,
    val data: String
) {
    override fun toString(): String {
        return "$name:$data"
    }
}