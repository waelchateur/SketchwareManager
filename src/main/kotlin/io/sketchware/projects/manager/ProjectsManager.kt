package io.sketchware.projects.manager

import io.sketchware.projects.model.SketchwareDirs
import io.sketchware.projects.model.SketchwareProject
import io.sketchware.projects.model.SketchwareProjectBaseInfo
import io.sketchware.projects.model.SketchwareProjectResources
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class ProjectsManager(private val dirs: SketchwareDirs) {
    val projects: List<SketchwareProject>
        get() {
            val array = ArrayList<SketchwareProject>()
            dirs.myscList.listFiles()?.forEach {
                val baseInfo: SketchwareProjectBaseInfo =
                        Json.decodeFromString(String(ProjectFileDecryptor.decrypt("${it.path}/project")))
                val resources = getResources(baseInfo.scId.toInt())
                val dataDir = File("${dirs.data?.absoluteFile}/${baseInfo.scId.toInt()}")
                val bakDir = File("${dirs.bak?.absoluteFile}/${baseInfo.scId.toInt()}")
                val myscDir = File("${dirs.mysc?.absoluteFile}/${baseInfo.scId.toInt()}")
                array.add(SketchwareProject(baseInfo, File("${it.path}/project"), resources, dataDir, bakDir, myscDir))
            }
            return array
        }

    fun getResources(projectId: Int): SketchwareProjectResources = with(dirs.resources) {
        val fonts = File("${fonts?.absolutePath}/$projectId").listFiles()
        val images = File("${images?.absolutePath}/$projectId").listFiles()
        val icons = File("${icons?.absolutePath}/$projectId").listFiles()
        val sounds = File("${sounds?.absolutePath}/$projectId").listFiles()
        return SketchwareProjectResources(fonts?.toList(), images?.toList(), sounds?.toList(), icons?.toList())
    }

    fun getById(id: Int): SketchwareProject {
        val baseInfo: SketchwareProjectBaseInfo =
                Json.decodeFromString(String(ProjectFileDecryptor.decrypt("${dirs.myscList}/$id/project")))
        val resources = getResources(baseInfo.scId.toInt())
        val dataDir = File("${dirs.data?.absoluteFile}/${baseInfo.scId.toInt()}")
        val bakDir = File("${dirs.bak?.absoluteFile}/${baseInfo.scId.toInt()}")
        val myscDir = File("${dirs.mysc?.absoluteFile}/${baseInfo.scId.toInt()}")
        return SketchwareProject(baseInfo, File("${dirs.myscList}/$id/project"), resources, dataDir, bakDir, myscDir)
    }

    val nextFreeId: Int get() = getFreeId(601)

    private fun getFreeId(startId: Int): Int {
        dirs.myscList.listFiles()!!.forEach {
            if (it.name == startId.toString())
                return getFreeId(startId + 1)
        }
        return startId
    }

}