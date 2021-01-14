package io.sketchware.models.sketchware

import kotlinx.serialization.Serializable

@Serializable
data class SketchwareBlock(
    val color: Int,
    val id: String,
    val nextBlock: Int,
    val opCode: String,
    val parameters: List<String>,
    val spec: String,
    val subStack1: Int,
    val subStack2: Int,
    val type: String,
    val typeName: String
)