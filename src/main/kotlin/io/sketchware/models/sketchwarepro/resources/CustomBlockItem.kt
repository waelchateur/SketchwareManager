package io.sketchware.models.sketchwarepro.resources

import io.sketchware.utils.StringNumberSerializer
import kotlinx.serialization.Serializable

/**
 * Class with data about custom block.
 */
@Serializable
data class CustomBlock(
    var typeName: String,
    /**
     * Hexadecimal string with block's color.
     */
    var color: String,
    /**
     * Block name
     */
    var name: String,
    /**
     * It stores data about the group that contains the block.
     */
    @Serializable(StringNumberSerializer::class)
    var palette: Int,
    var spec: String,
    var type: String,
    /**
     * Source code of block (it logic).
     */
    var code: String
)