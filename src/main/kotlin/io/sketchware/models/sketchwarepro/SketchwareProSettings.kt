package io.sketchware.models.sketchwarepro

import kotlinx.serialization.SerialName

data class SketchwareProSettings(
    val palletteDir: String,
    val blockDir: String,
    @SerialName("built-in-blocks")
    val builtInBlocks: Boolean,
    @SerialName("always-show-blocks")
    val alwaysShowBlocks: Boolean
)