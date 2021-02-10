package io.sketchware.models.exportable

import kotlinx.serialization.Serializable

@Serializable
data class ProjectExportableConfig(
    val generationTime: Long,
    val projectType: ProjectType
)