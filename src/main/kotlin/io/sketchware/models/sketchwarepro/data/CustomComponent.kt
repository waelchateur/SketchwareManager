package io.sketchware.models.sketchwarepro.data

import io.sketchware.utils.StringNumberSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO comments
@Serializable
data class CustomComponent(
    @Serializable(StringNumberSerializer::class)
    val icon: Int,
    @SerialName("class")
    val `class`: String,
    val description: String,
    val defineAdditionalVar: String,
    val typeName: String,
    val id: String,
    val url: String,
    val name: String,
    val additionalVar: String,
    val varName: String,
    val imports: String,
    val buildClass: String
)