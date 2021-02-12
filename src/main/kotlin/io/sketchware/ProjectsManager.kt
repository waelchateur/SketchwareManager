package io.sketchware

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.SketchwareFolderError
import io.sketchware.models.exceptions.SketchwareProConfigNotFoundException
import io.sketchware.models.sketchware.ProjectConfig
import io.sketchware.models.sketchware.ProjectFilesLocations
import io.sketchware.models.sketchwarepro.SketchwareProSettings
import io.sketchware.project.SketchwareProProject
import io.sketchware.project.SketchwareProject
import io.sketchware.resources.blocks.SketchwareProCustomBlocksManager
import io.sketchware.utils.*
import io.sketchware.utils.getListFiles
import io.sketchware.utils.serialize
import java.io.File

class ProjectsManager(private val sketchwareFolder: File) {

    init {
        if (sketchwareFolder.isFile)
            throw SketchwareFolderError(sketchwareFolder.path)
    }

    constructor(sketchwareFolder: String) : this(File(sketchwareFolder))

    suspend fun getProjects(): List<SketchwareProject>? {
        return File(sketchwareFolder, "mysc/list").getListFiles()?.map {
            if (isSketchwareProProject(it.name.toInt()))
                SketchwareProject(ProjectFilesLocations.defaultSketchwareProject(sketchwareFolder, it.name.toInt()))
            else SketchwareProProject(
                ProjectFilesLocations.defaultSketchwareProProject(sketchwareFolder, it.name.toInt())
            )
        }
    }

    val nextFreeId: Int get() = getFreeId(601)

    fun isSketchwareProProject(id: Int) =
        File(sketchwareFolder, "/data/$id/project_config").exists()

    suspend fun importExportable(folder: File) {
        val cfg = File(folder, "projectConfig.config")
            .readFile()
            .serialize<ProjectConfig>()
            .copy(projectId = nextFreeId)
        createProject(cfg)
        val skProCustomDir = File(folder, "requirements/sketchwarePro/custom")
        if(skProCustomDir.exists()) {
            getSketchwareProSettingsOrDefault().apply {
                SketchwareCustomUtils.insertBlocks(
                    SketchwareProCustomBlocksManager(File(blocksFilePath), File(paletteFilePath)),
                    File(skProCustomDir, "blocks.json").readFile().serialize()
                )
                //FIXME
                //SketchwareCustomUtils.insertMenus(SketchwareProCustomMenusManager())
            }
        }

    }

    suspend fun getSketchwareProSettingsOrNull(): SketchwareProSettings? {
        return File(sketchwareFolder, "data/settings.json").readFileOrNull()?.serialize()
    }

    suspend fun getSketchwareProSettings() =
        getSketchwareProSettingsOrNull()
            ?: throw SketchwareProConfigNotFoundException("${sketchwareFolder.path}/data/settings.json")

    suspend fun getSketchwareProSettingsOrDefault() =
        getSketchwareProSettingsOrNull() ?: SketchwareProSettings(
            "${sketchwareFolder.path}/resources/block/My Block/palette.json",
            "${sketchwareFolder.path}/resources/block/My Block/block.json",
            false,
            alwaysShowBlocks = false
        )

    suspend fun createProject(config: ProjectConfig) {
        File(sketchwareFolder, "mysc/list/${config.projectId}/project")
            .writeFile(FileEncryptor.encrypt(config.toJson().toByteArray()))
    }

    private fun getFreeId(startId: Int): Int {
        File(File(sketchwareFolder, "mysc"), "list").listFiles()?.forEach {
            if (it.name == startId.toString())
                return getFreeId(startId + 1)
        }
        return startId
    }

}