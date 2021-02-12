package io.sketchware.models.sketchware

import io.sketchware.utils.StringNumberSerializer
import kotlinx.serialization.Serializable

@Serializable
data class SketchwareBlock(
    val color: Int,
    @Serializable(StringNumberSerializer::class)
    val id: Int,
    val nextBlock: Int,
    val opCode: String,
    val parameters: List<String>,
    val spec: String,
    val subStack1: Int,
    val subStack2: Int,
    val type: String,
    val typeName: String
)