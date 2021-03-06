package io.sketchware.models.sketchware.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SketchwareEvent(
    @SerialName("eventName")
    val name: String,
    @SerialName("eventType")
    val type: Int,
    val targetId: String,
    val targetType: Int
)
