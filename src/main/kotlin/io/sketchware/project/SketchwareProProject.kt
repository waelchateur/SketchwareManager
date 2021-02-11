package io.sketchware.project

import io.sketchware.models.exceptions.ActivitiesNotFoundException
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

/**
 * Responsible for working with the project, stores instances of logic,
 * view, resource, library managers. Also a unique API for working with other parts of the project.
 * Has a unique API for working with the SketchwarePro project settings.
 * @param filesLocations locations of project files.
 */
open class SketchwareProProject(
    private val filesLocations: ProjectFilesLocations
) : SketchwareProject(filesLocations) {

    constructor(sketchwareFolder: File, projectId: Int) :
            this(ProjectFilesLocations.defaultSketchwareProject(sketchwareFolder, projectId))

    constructor(sketchwareFolderPath: String, projectId: Int) :
            this(File(sketchwareFolderPath), projectId)

    private fun getData() = filesLocations.data as SketchwareProProjectDataFiles

    /**
     * Gets rules of proguard.
     * @return [String] of proguard rules file or null if file doesn't exist.
     */
    suspend fun getProguardRulesOrNull(): String? {
        return getData().proguardRulesFile.readFileOrNull()?.let {
            String(it)
        }
    }

    /**
     * Gets rules of proguard.
     * @return [String] of proguard rules file or some IO exception if there is a problem.
     */
    suspend fun getProguardRules(): String {
        return String(getData().proguardRulesFile.readFile())
    }

    /**
     * Gets proguard config.
     * @return [ProguardConfig] or null if file with config doesn't exist.
     */
    suspend fun getProguardConfigOrNull(): ProguardConfig? {
        return String(
            getData().proguardConfigFile.readFile()
        ).serialize()
    }

    /**
     * Sets the proguard rules to [SketchwareProProjectDataFiles.proguardRulesFile].
     * @param [rules] new proguard rules.
     */
    suspend fun setProguardRules(rules: String) {
        getData().proguardRulesFile.writeFile(rules.toByteArray())
    }

    /**
     * Sets proguard config to [SketchwareProProjectDataFiles.proguardConfigFile].
     * @param [config] new config to save.
     */
    suspend fun setProguardConfig(config: ProguardConfig) {
        getData().proguardConfigFile.writeFile(config.toJson().toByteArray())
    }

    /**
     * Gets full [Exportable] of all connected with project files (except of bak, mysc builds).
     * It also saves custom menus and blocks to import it somewhere later.
     * @param paletteFile palette file (by default located at ../.sketchware/resources/block/My Block/palette.json,
     * but be careful this path can be changed by user. Use file at ../.sketchware/data/settings.json).
     * @param blocksFile file with blocks (by default located at ../.sketchware/resources/block/My Block/block.json,
     * but be careful this path can be changed by user. Use file at ../.sketchware/data/settings.json).
     * @param menuBlockFile file with custom menus
     * (by default located at ../.sketchware/resources/Menu Block/block.json, but be careful this path can be changed
     * by user. Use file at ../.sketchware/data/settings.json)
     * @param menuDataFile file with data about custom menus
     * (by default located at ../.sketchware/resources/Menu Block/data.json, but be careful this path can be changed
     * by user. Use file at ../.sketchware/data/settings.json)
     * @return [Exportable] with all files which can be saved as folder or zip.
     */
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

    @Throws(ActivitiesNotFoundException::class)
    private suspend fun getCustomBlocksRequirements(
        paletteFile: File,
        blocksFile: File,
    ): List<CustomBlockGroup> {
        val blocksToSave = mutableListOf<String>()

        fileManager.getActivities().forEach { file ->
            val activityName = "${file.fileName.capitalize()}Activity"
            val events = logicManager.getEventsOrNull(activityName)
            val moreblocks = logicManager.getMoreblocksOrNull(activityName)
            val blocksList = mutableListOf<SketchwareBlock>()
            events?.forEach { event ->
                logicManager.getEventLogicOrNull(activityName, event.targetId, event.name)
                    ?.let {
                        blocksList.addAll(it)
                    }
            }
            moreblocks?.forEach { moreblock ->
                logicManager.getMoreblockLogicOrNull(activityName, moreblock.name)?.let {
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

    @Throws(ActivitiesNotFoundException::class)
    private suspend fun getCustomMenusRequirements(
        menuBlockFile: File,
        menuDataFile: File
    ): List<CustomMenu> {
        val menusToSave = mutableListOf<String>()
        fileManager.getActivities().forEach { file ->
            val activityName = "${file.fileName.capitalize()}Activity"
            val events = logicManager.getEventsOrNull(activityName)
            val moreblocks = logicManager.getMoreblocksOrNull(activityName)
            val blocksList = mutableListOf<SketchwareBlock>()
            events?.forEach { event ->
                logicManager.getEventLogicOrNull(activityName, event.targetId, event.name)
                    ?.let {
                        blocksList.addAll(it)
                    }
            }
            moreblocks?.forEach { moreblock ->
                logicManager.getMoreblockLogicOrNull(activityName, moreblock.name)?.let {
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