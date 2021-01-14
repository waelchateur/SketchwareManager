package io.sketchware.java.wrapper

import io.sketchware.SketchwareProjects
import io.sketchware.project.SketchwareProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class SketchwareProjects : CoroutineScope {

    private val manager: SketchwareProjects

    constructor(sketchcodeFolder: File) {
        manager = SketchwareProjects(sketchcodeFolder)
    }

    constructor(sketchcodeFolder: String) {
        manager = SketchwareProjects(sketchcodeFolder)
    }

    fun getNextFreeId() = manager.nextFreeId
    fun isSketchwareProProject(id: Int) = manager.isSketchwareProProject(id)

    fun interface ProjectsLoadedCallback {
        fun onLoad(projects: List<SketchwareProject>?)
    }

    fun getProjects(callback: ProjectsLoadedCallback) = launch {
        callback.onLoad(manager.getProjects())
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()
}