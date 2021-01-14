package io.sketchware.project

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.sketchware.ProjectConfig
import io.sketchware.models.sketchware.ProjectDestination
import io.sketchware.models.sketchware.ProjectFilesLocations
import io.sketchware.models.sketchware.SketchwareProProjectDataFiles
import io.sketchware.models.sketchwarepro.ProguardConfig
import io.sketchware.project.data.file.FileManager
import io.sketchware.project.data.library.LibraryManager
import io.sketchware.project.data.logic.LogicManager
import io.sketchware.project.data.resource.ResourcesManager
import io.sketchware.project.data.view.ViewManager
import io.sketchware.utils.*
import java.io.File

open class SketchwareProject(
    private val filesLocations: ProjectFilesLocations
) {

    constructor(sketchwareFolder: File, projectId: Int) : this(
        ProjectFilesLocations.defaultSketchwareProject(
            sketchwareFolder,
            projectId
        )
    )

    constructor(sketchwareFolderPath: String, projectId: Int) : this(File(sketchwareFolderPath), projectId)

    /**
     * Deletes project and all files which connected with it.
     */
    open suspend fun delete() = with(filesLocations) {
        deleteFiles(
            mysc.configFolder, mysc.buildFolder,
            data.dataFolder, backup.backupFolder,
            resources.fonts, resources.icons, resources.images, resources.sounds
        )
    }

    open suspend fun getConfig(): ProjectConfig {
        return String(
            FileEncryptor.decrypt(filesLocations.mysc.configFile.readBytes())
        ).serialize()
    }

    open suspend fun editConfig(builder: ProjectConfig.() -> Unit) = editConfig(getConfig().apply(builder))

    open suspend fun editConfig(config: ProjectConfig) {
        filesLocations.mysc.configFile.writeFile(
            FileEncryptor.encrypt(config.toJson().toByteArray())
        )
    }

    /**
     * Automatically based on [filesLocations] variable is determined by [LogicManager].
     * Responsible for the custom views, activity views.
     * @return [FileManager] based on [filesLocations] variable paths.
     */
    open val fileManager by lazy { FileManager(filesLocations.data.fileFile) }

    /**
     * Automatically based on [filesLocations] variable is determined by [LibraryManager].
     * Responsible for the included libraries in the project.
     * @return [LibraryManager] based on [filesLocations] variable paths.
     */
    open val libraryManager by lazy { LibraryManager(filesLocations.data.libraryFile) }

    /**
     * Automatically based on [filesLocations] variable is determined by [LibraryManager].
     * Responsible for the logic of the project (events, moreblocks, etc.).
     * @return [LogicManager] based on [filesLocations] variable paths.
     */
    open val logicManager by lazy { LogicManager(filesLocations.data.logicFile) }

    /**
     * Automatically based on [filesLocations] variable is determined by [ResourcesManager].
     * Responsible for the resources of the project (fonts, images, sounds)
     * @return [ResourcesManager] based on [filesLocations] variable paths.
     */
    open val resourcesManager by lazy { ResourcesManager(filesLocations.data.resourceFile) }


    /**
     * Automatically based on [filesLocations] variable is determined by [ResourcesManager].
     * Responsible for the views content (xml).
     * @return [ResourcesManager] based on [filesLocations] variable paths.
     */
    open val viewManager by lazy { ViewManager(filesLocations.data.viewFile) }

    /**
     * Clones a project with a minimal set of files (excludes backup and build files in mysc folder).
     * @param newId New identifier for the project, make sure there is no similar project with the same id.
     * @param destination destinations folders for cloning
     */
    open suspend fun clone(newId: Int, destination: ProjectDestination) = with(filesLocations) {
        val currentConfig: ProjectConfig = String(
            FileEncryptor.decrypt(filesLocations.mysc.configFile.readBytes())
        ).serialize()
        val newConfig = currentConfig.copy(projectId = "$newId")
        destination.projectFile.writeBytes(
            FileEncryptor.encrypt(newConfig.toJson().toByteArray())
        )
        data.dataFolder.copyFolder(destination.projectDataFolder)
        resources.sounds.copyFolder(destination.projectResources.sounds)
        resources.images.copyFolder(destination.projectResources.images)
        resources.fonts.copyFolder(destination.projectResources.fonts)
        resources.icons.copyFolder(destination.projectResources.icons)
    }

    open suspend fun export(zipDestination: File) {
    }

}

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

}