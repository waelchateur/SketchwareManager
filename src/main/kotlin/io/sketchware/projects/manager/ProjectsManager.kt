package io.sketchware.projects.manager

import io.sketchware.projects.exception.ConfigException
import io.sketchware.projects.model.SketchwareConfig
import io.sketchware.projects.model.SketchwareProject
import io.sketchware.projects.model.SketchwareProjectBaseInfo
import io.sketchware.projects.model.SketchwareResourcesPaths
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class ProjectsManager private constructor() {

    private lateinit var config: SketchwareConfig

    constructor(builder: SketchwareConfig.() -> Unit) : this() {
        config = SketchwareConfig().apply(builder)
    }

    /**
     * Puts standard links to resources and data.
     * @param path base path for sketchware folder
     */
    constructor(path: String) : this({
        if (!File(path).exists())
            throw ConfigException("$path not exists")
        myscPath = "$path/mysc"
        myscListPath = "$myscPath/list"
        dataPath = "$path/data"
        bakPath = "$path/bak"
        resources = SketchwareResourcesPaths(
            "$path/resources/icons",
            "$path/resources/images",
            "$path/resources/fonts",
            "$path/resources/sounds"
        )
    })

    constructor(config: SketchwareConfig): this() {
        this.config = config
    }

    val projects: List<SketchwareProject>
        get() {
            val array = ArrayList<SketchwareProject>()
            config.myscList?.listFiles()?.forEach {
                if (it.isDirectory) {
                    val baseInfo: SketchwareProjectBaseInfo =
                        Json.decodeFromString(String(ProjectFileDecryptor.decrypt("${it.path}/project")))
                    val resources = getResources(baseInfo.scId.toInt())
                    val dataDir = File("${config.data?.absoluteFile}/${baseInfo.scId.toInt()}")
                    val bakDir = File("${config.bak?.absoluteFile}/${baseInfo.scId.toInt()}")
                    val myscDir = File("${config.mysc?.absoluteFile}/${baseInfo.scId.toInt()}")
                    array.add(
                        SketchwareProject(
                            baseInfo,
                            File("${it.path}/project"),
                            resources,
                            dataDir,
                            bakDir,
                            myscDir
                        )
                    )
                }
            }
            return array
        }

    fun getResources(projectId: Int): SketchwareResourcesPaths = with(config.resources) {
        val fonts = File("${this?.fonts?.absolutePath}/$projectId")
        val images = File("${this?.images?.absolutePath}/$projectId")
        val icons = File("${this?.icons?.absolutePath}/$projectId")
        val sounds = File("${this?.sounds?.absolutePath}/$projectId")
        return SketchwareResourcesPaths(icons, images, fonts, sounds)
    }

    fun getById(id: Int): SketchwareProject {
        val baseInfo: SketchwareProjectBaseInfo =
            Json.decodeFromString(String(ProjectFileDecryptor.decrypt("${config.myscList}/$id/project")))
        val resources = getResources(baseInfo.scId.toInt())
        val dataDir = File("${config.data?.absolutePath}/${baseInfo.scId.toInt()}")
        val bakDir = File("${config.bak?.absolutePath}/${baseInfo.scId.toInt()}")
        val myscDir = File("${config.mysc?.absolutePath}/${baseInfo.scId.toInt()}")
        return SketchwareProject(
            baseInfo,
            File("${config.myscList?.absolutePath}/$id/project"),
            resources,
            dataDir,
            bakDir,
            myscDir
        )
    }

    val nextFreeId: Int get() = getFreeId(601)

    private fun getFreeId(startId: Int): Int {
        config.myscList?.listFiles()!!.forEach {
            if (it.name == startId.toString())
                return getFreeId(startId + 1)
        }
        return startId
    }

}