package io.sketchware.projects.model

import io.sketchware.projects.exception.SketchwareEncryptException
import io.sketchware.projects.manager.ProjectFileDecryptor
import io.sketchware.projects.manager.exception.ProjectInfoFileNotFoundException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

data class SketchwareProject(
    /** Base info of project (name, package, etc) */
    val infoSketchware: SketchwareProjectBaseInfo,
    /** Project info file */
    val infoFile: File,
    /** class with lists of files paths */
    val resources: SketchwareProjectResources,
    /** Folder with project data */
    val dataDir: File?,
    /** Folder with project data */
    val bakDir: File?,
    /** Folder with builds & apks and etc */
    val myscDir: File?
) {

    init {
        if (!infoFile.exists())
            throw ProjectInfoFileNotFoundException(infoFile.path)
    }

    /**
     * Delete sketchware projects with all info
     */
    fun delete() {
        infoFile.delete()
        dataDir?.deleteRecursively()
        bakDir?.deleteRecursively()
        resources.fonts?.forEach { it.delete() }
        resources.sounds?.forEach { it.delete() }
        resources.icons?.forEach { it.delete() }
        resources.images?.forEach { it.delete() }
    }

    /**
     * Copy project with new id
     */
    fun copy(projectId: Int, sketchwareDirs: SketchwareDirs) {
        val information = infoSketchware.copy(scId = "$projectId")
        create(information, sketchwareDirs.myscList)

        sketchwareDirs.apply {
            sketchwareDirs.bak?.let { bakDir?.copyRecursively(it) }
            sketchwareDirs.data?.let { dataDir?.copyRecursively(it) }
            sketchwareDirs.mysc?.let { myscDir?.copyRecursively(it) }
        }
        resources.icons?.forEach {
            sketchwareDirs.resources.icons?.let { it1 -> it.copyTo(it1) }
        }
        resources.fonts?.forEach {
            sketchwareDirs.resources.fonts?.let { it1 -> it.copyTo(it1) }
        }
        resources.images?.forEach {
            sketchwareDirs.resources.images?.let { it1 -> it.copyTo(it1) }
        }
        resources.sounds?.forEach {
            sketchwareDirs.resources.sounds?.let { it1 -> it.copyTo(it1) }
        }
    }

    fun edit(infoSketchware: SketchwareProjectBaseInfo): SketchwareProject {
        ProjectFileDecryptor.encrypt(Json.encodeToString(infoSketchware))?.let { infoFile.writeBytes(it) }
        return this.copy(infoSketchware = infoSketchware)
    }

    companion object {

        /**
         * Create project
         * @param infoSketchware info of project
         * @throws SketchwareEncryptException if an internal error happened
         */
        fun create(infoSketchware: SketchwareProjectBaseInfo, infoFile: File) {
            infoFile.writeBytes(
                ProjectFileDecryptor.encrypt(Json.encodeToString(infoSketchware)) ?: throw SketchwareEncryptException()
            )
        }

    }

}