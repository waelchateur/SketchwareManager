package io.sketchware.models.sketchwarepro.data

import io.sketchware.utils.StringBooleanSerializer
import kotlinx.serialization.Serializable

//TODO comment about class
@Serializable
data class CustomListener(
    val name: String,
    /**
     * Code of specific listener.
     */
    val code: String,
    @Serializable(StringBooleanSerializer::class)
    //TODO comment i don't fucking understand what is this
    val s: Boolean,
    //TODO comment about how it splits and etc i don't fucking understand
    // this shit is ununderstandable
    val imports: String
)