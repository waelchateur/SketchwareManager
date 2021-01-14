package io.sketchware.java.wrapper.project.data.resource

import io.sketchware.models.sketchware.data.SketchwareProjectResource
import io.sketchware.project.data.resource.ResourcesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ResourcesManager(file: File): CoroutineScope {
    private val manager = ResourcesManager(file)

    fun interface OnResourcesLoadedCallback {
        fun onLoad(resources: List<SketchwareProjectResource>?)
    }

    fun getImages(callback: OnResourcesLoadedCallback) = launch {
        callback.onLoad(manager.getImages())
    }

    fun getFonts(callback: OnResourcesLoadedCallback) = launch {
        callback.onLoad(manager.getFonts())
    }

    fun getSounds(callback: OnResourcesLoadedCallback) = launch {
        callback.onLoad(manager.getSounds())
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()
}