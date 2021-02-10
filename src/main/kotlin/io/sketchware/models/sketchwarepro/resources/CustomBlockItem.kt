package io.sketchware.models.sketchwarepro.resources

import kotlinx.serialization.Serializable

@Serializable
data class CustomBlock(
    var typeName: String,
    var color: String,
    var name: String,
    var palette: String,
    var spec: String,
    var type: String,
    var code: String
)