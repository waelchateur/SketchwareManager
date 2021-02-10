package io.sketchware.project

import io.sketchware.models.sketchware.ProjectFilesLocations
import io.sketchware.models.sketchware.SketchwareBlock
import io.sketchware.models.sketchware.SketchwareProProjectDataFiles
import io.sketchware.models.sketchwarepro.ProguardConfig
import io.sketchware.models.sketchwarepro.resources.CustomBlockGroup
import io.sketchware.models.sketchwarepro.resources.CustomMenu
import io.sketchware.resources.blocks.SketchwareProCustomBlocksManager
import io.sketchware.resources.menu.SketchwareProCustomMenusManager
import io.sketchware.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

open class SketchwareProProject(
    private val filesLocations: ProjectFilesLocations
) : SketchwareProject(filesLocations) {

    constructor(sketchwareFolder: File, projectId: Int) :
            this(ProjectFilesLocations.defaultSketchwareProject(sketchwareFolder, projectId))

    constructor(sketchwareFolderPath: String, projectId: Int) :
            this(File(sketchwareFolderPath), projectId)

    private fun getData() = filesLocations.data as SketchwareProProjectDataFiles

    suspend fun getProguardRules(): String {
        return String(
            getData().proguardRulesFile.readFile()
        )
    }

    suspend fun getProguardConfig(): ProguardConfig? {
        return String(
            getData().proguardConfigFile.readFile()
        ).serialize()
    }

    suspend fun setProguardRules(rules: String) {
        getData().proguardRulesFile.writeFile(rules.toByteArray())
    }

    suspend fun setProguardConfig(config: ProguardConfig) {
        getData().proguardConfigFile.writeFile(config.toJson().toByteArray())
    }

    suspend fun exportable(
        paletteFile: File,
        blocksFile: File,
        menuBlockFile: File,
        menuDataFile: File
    ) = withContext(Dispatchers.IO) {
        val customBlocks = getCustomBlocksRequirements(paletteFile, blocksFile)
        val customMenus = getCustomMenusRequirements(menuBlockFile, menuDataFile)
        return@withContext Exportable {
            add(
                ExportableItem(
                    "requirements/sketchwarePro/custom", "blocks.json",
                    customBlocks.toJson().toByteArray()
                )
            )
            add(
                ExportableItem(
                    "requirements/sketchwarePro/custom",
                    "menus.json",
                    customMenus.toJson().toByteArray()
                )
            )
        }.also { it + getDefaultExportable() }
    }

    private suspend fun getCustomBlocksRequirements(
        paletteFile: File,
        blocksFile: File,
    ): List<CustomBlockGroup> {
        val blocksToSave = mutableListOf<String>()

        fileManager.getActivities()?.forEach { file ->
            val activityName = "${file.fileName.capitalize()}Activity"
            val events = logicManager.getEvents(activityName)
            val moreblocks = logicManager.getMoreblocks(activityName)
            val blocksList = mutableListOf<SketchwareBlock>()
            events?.forEach { event ->
                logicManager.getEventLogic(activityName, event.targetId, event.name)
                    ?.let {
                        blocksList.addAll(it)
                    }
            }
            moreblocks?.forEach { moreblock ->
                logicManager.getMoreblockLogic(activityName, moreblock.name)?.let {
                    blocksList.addAll(it)
                }
            }

            blocksList.forEach { block ->
                if (!OpCodes.logic.contains(block.opCode))
                    blocksToSave += block.opCode
            }
        }

        return SketchwareProCustomBlocksManager(blocksFile, paletteFile).getCustomBlocks().filter {
            blocksToSave.contains(it.name)
        }
    }

    private suspend fun getCustomMenusRequirements(
        menuBlockFile: File,
        menuDataFile: File
    ): List<CustomMenu> {
        val menusToSave = mutableListOf<String>()
        fileManager.getActivities()?.forEach { file ->
            val activityName = "${file.fileName.capitalize()}Activity"
            val events = logicManager.getEvents(activityName)
            val moreblocks = logicManager.getMoreblocks(activityName)
            val blocksList = mutableListOf<SketchwareBlock>()
            events?.forEach { event ->
                logicManager.getEventLogic(activityName, event.targetId, event.name)
                    ?.let {
                        blocksList.addAll(it)
                    }
            }
            moreblocks?.forEach { moreblock ->
                logicManager.getMoreblockLogic(activityName, moreblock.name)?.let {
                    blocksList.addAll(it)
                }
            }

            blocksList.forEach { block ->
                Regex("%m\\\\.(\\w*)", RegexOption.DOT_MATCHES_ALL).findAll(block.spec).iterator().forEach {
                    it.groups[1]?.value?.let { it1 -> menusToSave.add(it1) }
                }
            }

        }

        return SketchwareProCustomMenusManager(menuBlockFile, menuDataFile).getCustomMenus().filter {
            menusToSave.contains(it.id)
        }
    }

}