package io.sketchware.models.sketchwarepro.resources

import kotlinx.serialization.Serializable

@Serializable
data class BlockInputMenu(
    val id: String,
    val name: String
)