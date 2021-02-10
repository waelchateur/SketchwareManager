package io.sketchware.models.sketchwarepro.resources

import kotlinx.serialization.Serializable

@Serializable
data class CustomBlockGroup(
    var groupId: Int,
    var name: String,
    var hexColor: String,
    var blocks: List<CustomBlock>
)
