package io.sketchware.projects.model

import io.sketchware.projects.exception.SketchwareEncryptException
import io.sketchware.projects.manager.ProjectFileDecryptor
import io.sketchware.projects.manager.exception.ProjectInfoFileNotFoundException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

data class SketchwareProject(
    /** Base info of project (name, package, etc) */
    val info: SketchwareProjectBaseInfo,
    /** Project info file */
    val infoFile: File,
    /** class with lists of files paths */
    val resources: SketchwareResourcesPaths,
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
        println("Project delete: ${infoFile.path} info file, data dir - ${dataDir?.path}")
        infoFile.parentFile.deleteRecursively()
        infoFile.delete()
        dataDir?.deleteRecursively()
        bakDir?.deleteRecursively()
        resources.fonts?.deleteRecursively()
        resources.sounds?.deleteRecursively()
        resources.icons?.deleteRecursively()
        resources.images?.deleteRecursively()
    }

    /**
     * Copy project with new id
     * @param config is destination dirs for copying
     */
    fun copy(projectId: Int, config: SketchwareConfig) {
        requireNotNull(config.myscList)

        val information = info.copy(scId = "$projectId")

        config.apply {
            bak?.mkdirs()
            data?.mkdirs()
            mysc?.mkdirs()
            println(myscList?.parent)
            File(myscList!!.parent).mkdirs()

            if (bak?.exists() == true && bakDir?.exists() == true)
                bakDir.copyRecursively(bak!!, true)
            if (data?.exists() == true && dataDir?.exists() == true)
                dataDir.copyRecursively(data!!, true)
            if (mysc?.exists() == true && myscDir?.exists() == true)
                myscDir.copyRecursively(mysc!!, true)
        }

        if (resources.icons?.exists() == true && config.resources?.icons?.exists() == true)
            config.resources?.icons?.let { resources.icons.copyRecursively(it) }
        if(resources.fonts?.exists() == true && config.resources?.fonts?.exists() == true)
            config.resources?.fonts?.let { resources.fonts.copyRecursively(it) }
        if(resources.images?.exists() == true && config.resources?.images?.exists() == true)
            config.resources?.images?.let { resources.images.copyRecursively(it) }
        if(resources.sounds?.exists() == true && config.resources?.sounds?.exists() == true)
            config.resources?.sounds?.let { resources.sounds.copyRecursively(it) }
        return create(information, File(config.myscList!!.path))
    }

    fun edit(infoSketchware: SketchwareProjectBaseInfo): SketchwareProject {
        ProjectFileDecryptor.encrypt(Json.encodeToString(infoSketchware))?.let { infoFile.writeBytes(it) }
        return this.copy(info = infoSketchware)
    }

    companion object {

        /**
         * Create project
         * @param infoSketchware info of project
         * @throws SketchwareEncryptException if an internal error happened
         */
        fun create(infoSketchware: SketchwareProjectBaseInfo, infoFile: File) {
            infoFile.writeBytes(
                ProjectFileDecryptor.encrypt(Json.encodeToString(infoSketchware))
                    ?: throw SketchwareEncryptException()
            )
        }

    }

    override fun toString(): String {
        return """Project with id #${info.scId}:
                        Project info:
            | App name: ${info.myAppName}
            | Version name: ${info.scVerName}
            | Version code: ${info.scVerCode}
            | Package name: ${info.myScPkgName}
            | Project name: ${info.myWsName}
            | Build with version: ${info.sketchwareVer}
                         Paths:
            | Info file location: ${infoFile.path}
            | Data dir location: ${dataDir?.path}
            | Bak dir location: ${bakDir?.path}
            | MYSC dir location: ${myscDir?.path}
            | Resources:
            |   * Icons: ${resources.icons?.path}
            |   * Images: ${resources.images?.path}
            |   * Fonts: ${resources.fonts?.path}
            |   * Sounds: ${resources.sounds?.path}
        """.trimMargin()
    }

}

