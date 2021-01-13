package io.sketchware.models.sketchwarepro

import kotlinx.serialization.Serializable

@Serializable
data class ProguardConfig(
    /**
     * String representation of boolean.
     * Responsible for status of Proguard.
     */
    val enabled: String,
    /**
     * String representation of boolean.
     * Responsible for the on/off state of the debug file generations.
     */
    val debug: String
)