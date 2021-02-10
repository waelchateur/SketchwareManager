package io.sketchware.models.sketchwarepro.resources

import kotlinx.serialization.Serializable

@Serializable
data class Palette(
    val color: String,
    val name: String
)