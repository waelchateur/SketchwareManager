package io.sketchware.models.sketchware

import io.sketchware.models.exportable.ProjectExportableConfig
import io.sketchware.models.exportable.ProjectType
import io.sketchware.utils.*
import io.sketchware.utils.getListFiles
import java.io.File

data class ProjectFilesLocations(
    val mysc: ProjectMyscFiles,
    val data: ProjectDataFiles,
    val backup: ProjectBackupFiles,
    val resources: SketchwareProjectResources
) {
    companion object {
        fun defaultSketchwareProject(sketchwareFolder: File, projectId: Int): ProjectFilesLocations {
            return ProjectFilesLocations(
                ProjectMyscFiles(
                    File(sketchwareFolder, "/mysc/list/$projectId/project"),
                    File(sketchwareFolder, "/mysc/list/$projectId"),
                    File(sketchwareFolder, "/mysc/$projectId")
                ),
                SketchwareProjectDataFiles(
                    File(sketchwareFolder, "/data/$projectId/file"),
                    File(sketchwareFolder, "/data/$projectId/library"),
                    File(sketchwareFolder, "/data/$projectId/logic"),
                    File(sketchwareFolder, "/data/$projectId/resource"),
                    File(sketchwareFolder, "/data/$projectId/view")
                ),
                ProjectBackupFiles(
                    File(sketchwareFolder, "/bak/$projectId/logic"),
                    File(sketchwareFolder, "/bak/$projectId/view")
                ),
                SketchwareProjectResources(
                    File(sketchwareFolder, "/resources/images/$projectId"),
                    File(sketchwareFolder, "/resources/icons/$projectId"),
                    File(sketchwareFolder, "/resources/fonts/$projectId"),
                    File(sketchwareFolder, "/resources/sounds/$projectId")
                )
            )
        }

        fun defaultSketchwareProProject(sketchwareFolder: File, projectId: Int): ProjectFilesLocations {
            return ProjectFilesLocations(
                ProjectMyscFiles(
                    File(sketchwareFolder, "/mysc/list/$projectId/project"),
                    File(sketchwareFolder, "/mysc/list/$projectId"),
                    File(sketchwareFolder, "/mysc/$projectId")
                ),
                SketchwareProProjectDataFiles(
                    File(sketchwareFolder, "/data/$projectId/file"),
                    File(sketchwareFolder, "/data/$projectId/library"),
                    File(sketchwareFolder, "/data/$projectId/logic"),
                    File(sketchwareFolder, "/data/$projectId/resource"),
                    File(sketchwareFolder, "/data/$projectId/view"),
                    File(sketchwareFolder, "/data/$projectId/proguard-rules.pro"),
                    File(sketchwareFolder, "/data/$projectId/proguard")
                ),
                ProjectBackupFiles(
                    File(sketchwareFolder, "/bak/$projectId/logic"),
                    File(sketchwareFolder, "/bak/$projectId/view")
                ),
                SketchwareProjectResources(
                    File(sketchwareFolder, "/resources/images/$projectId"),
                    File(sketchwareFolder, "/resources/icons/$projectId"),
                    File(sketchwareFolder, "/resources/fonts/$projectId"),
                    File(sketchwareFolder, "/resources/sounds/$projectId")
                )
            )
        }

    }

    suspend fun toExportable(): Exportable {
        val exportableItems = mutableListOf<ExportableItem>()
        val config = ProjectExportableConfig(
            System.currentTimeMillis(),
            if (data is SketchwareProProjectDataFiles) ProjectType.SketchwarePro
            else ProjectType.Sketchware
        )
        exportableItems.add(ExportableItem(fileName = "export.config", value = config.toJson().toByteArray()))
        exportableItems.addAll(Exportable.getItemsFromFolder(data.dataFolder))
        resources.fonts.getListFiles()?.forEach {
            exportableItems.add(ExportableItem("resources/fonts", it.name, it.readFile()))
        }
        resources.sounds.getListFiles()?.forEach {
            exportableItems.add(ExportableItem("resources/sounds", it.name, it.readFile()))
        }
        resources.images.getListFiles()?.forEach {
            exportableItems.add(ExportableItem("resources/images", it.name, it.readFile()))
        }
        resources.icons.getListFiles()?.forEach {
            exportableItems.add(ExportableItem("resources/icons", it.name, it.readFile()))
        }
        exportableItems.add(ExportableItem(fileName = "projectConfig.swe", value = mysc.configFile.readFile()))
        return Exportable(exportableItems)
    }

}

data class ProjectMyscFiles(
    val configFile: File,
    val configFolder: File,
    val buildFolder: File
)

abstract class ProjectDataFiles(
    open var fileFile: File,
    open var libraryFile: File,
    open var logicFile: File,
    open var resourceFile: File,
    open var viewFile: File,
    open var dataFolder: File = fileFile.parentFile
)

open class SketchwareProjectDataFiles(
    override var fileFile: File,
    override var libraryFile: File,
    override var logicFile: File,
    override var resourceFile: File,
    override var viewFile: File
) : ProjectDataFiles(fileFile, libraryFile, logicFile, resourceFile, viewFile)

open class SketchwareProProjectDataFiles(
    override var fileFile: File,
    override var libraryFile: File,
    override var logicFile: File,
    override var resourceFile: File,
    override var viewFile: File,
    var proguardRulesFile: File,
    var proguardConfigFile: File
) : ProjectDataFiles(fileFile, libraryFile, logicFile, resourceFile, viewFile)

open class ProjectBackupFiles(
    val logicFile: File,
    val viewFile: File,
    val backupFolder: File = logicFile.parentFile
)