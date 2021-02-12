package io.sketchware.models.sketchwarepro.data

import io.sketchware.utils.StringNumberSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO comment about class
@Serializable
data class CustomEvent(
    val headerSpec: String,
    @Serializable(StringNumberSerializer::class)
    val icon: Int,
    @SerialName("var")
    val variable: String,
    val description: String,
    val parameters: String,
    val name: String,
    val code: String,
    val listener: String
)