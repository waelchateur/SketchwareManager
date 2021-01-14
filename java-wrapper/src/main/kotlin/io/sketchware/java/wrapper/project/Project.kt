package io.sketchware.java.wrapper.project

import io.sketchware.java.wrapper.common.OnActionFinishedCallback
import io.sketchware.models.sketchware.ProjectConfig
import io.sketchware.models.sketchware.ProjectFilesLocations
import io.sketchware.project.SketchwareProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class SketchwareProject(filesLocations: ProjectFilesLocations) : CoroutineScope {
    private val manager = SketchwareProject(filesLocations)

    fun interface onConfigLoadedCallback {
        fun onLoad(config: ProjectConfig)
    }

    fun getConfig(onConfigLoaded: onConfigLoadedCallback) = launch {
        onConfigLoaded.onLoad(manager.getConfig())
    }

    fun editConfig(
        config: ProjectConfig,
        onActionFinishedCallback: OnActionFinishedCallback? = null
    ) = launch {
        manager.editConfig(config)
        onActionFinishedCallback?.onFinish()
    }

    fun delete(
        onActionFinishedCallback: OnActionFinishedCallback? = null
    ) = launch {
        manager.delete()
        onActionFinishedCallback?.onFinish()
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()
}