package io.sketchware.models.sketchwarepro

import io.sketchware.utils.StringBooleanSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ProguardConfig(
    /**
     * Responsible for status of Proguard.
     */
    @Serializable(StringBooleanSerializer::class)
    val enabled: Boolean,
    /**
     * Responsible for the on/off state of the debug file generations.
     */
    @Serializable(StringBooleanSerializer::class)
    val debug: Boolean
)