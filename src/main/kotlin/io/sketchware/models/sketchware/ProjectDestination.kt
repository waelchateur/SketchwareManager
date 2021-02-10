package io.sketchware.models.sketchware

import java.io.File

data class ProjectDestination(
    val projectFile: File,
    val projectDataFolder: File,
    val projectResources: SketchwareProjectResources
) {
    companion object {
        fun from(sketchwarePath: String, projectId: Int) = fromFolder(File(sketchwarePath), projectId)

        fun fromFolder(folder: File, projectId: Int): ProjectDestination {
            val resourcesFolder = File(folder, "resources")
            return ProjectDestination(
                File(folder, "mysc/list/$projectId/project"),
                File(folder, "data/$projectId"),
                SketchwareProjectResources(
                    File(resourcesFolder, "images/$projectId"),
                    File(resourcesFolder, "icons/$projectId"),
                    File(resourcesFolder, "fonts/$projectId"),
                    File(resourcesFolder, "sounds/$projectId")
                )
            )
        }

        fun createExportable(folder: File): ProjectDestination {
            return ProjectDestination(
                File(folder, "project.config"),
                File(folder, "data"),
                SketchwareProjectResources(
                    File(folder, "resources/images"),
                    File(folder, "resources/icons"),
                    File(folder, "resources/fonts"),
                    File(folder, "resources/sounds")
                )
            )
        }

    }
}