package io.sketchware.java.wrapper.project.data.file

import io.sketchware.java.wrapper.common.OnActionFinishedCallback
import io.sketchware.models.sketchware.data.SketchwareDataFile
import io.sketchware.project.data.file.FileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class FileManager(private val file: File) : CoroutineScope {
    private val manager = FileManager(file)

    fun interface OnSketchwareDataFileLoadedCallback {
        fun onLoad(data: List<SketchwareDataFile>?)
    }

    fun getActivities(callback: OnSketchwareDataFileLoadedCallback) = launch {
        callback.onLoad(manager.getActivities())
    }

    fun getCustomViews(callback: OnSketchwareDataFileLoadedCallback) = launch {
        callback.onLoad(manager.getCustomViews())
    }

    fun addCustomView(
        customView: SketchwareDataFile,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.addCustomView(customView)
        callback?.onFinish()
    }

    fun addActivity(
        activity: SketchwareDataFile,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.addActivity(activity)
        callback?.onFinish()
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()
}