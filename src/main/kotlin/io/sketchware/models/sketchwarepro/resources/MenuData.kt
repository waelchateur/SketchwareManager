package io.sketchware.models.sketchwarepro.resources

import kotlinx.serialization.Serializable

@Serializable
data class MenuData(
    val title: String,
    val value: String
)