package io.sketchware.models.sketchwarepro

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class with data about Sketchware Pro settings: where custom blocks/menus storing.
 */
@Serializable
data class SketchwareProSettings(
    /**
     * Path to palette file.
     * Primarily located at ../.sketchware/resources/block/My Block/palette.json,
     * but can be changed by user.
     */
    @SerialName("palletteDir")
    val paletteFilePath: String,
    /**
     * Path to block file.
     * Primarily located at ../.sketchware/resources/block/My Block/block.json,
     * but can be changed by user.
     */
    @SerialName("blockDir")
    val blocksFilePath: String,
    @SerialName("built-in-blocks")
    val builtInBlocks: Boolean,
    /**
     * Feature status data that shows all blocks
     * even if you don't have some components / variables.
     */
    @SerialName("always-show-blocks")
    val alwaysShowBlocks: Boolean
)