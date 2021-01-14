package io.sketchware.java.wrapper.project.data.library

import io.sketchware.java.wrapper.common.OnActionFinishedCallback
import io.sketchware.models.sketchware.data.SketchwareLibrary
import io.sketchware.project.data.library.LibraryManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class LibraryManager(file: File): CoroutineScope {
    private val manager = LibraryManager(file)

    fun interface OnLibrariesLoadedCallback {
        fun onLoad(libraries: List<SketchwareLibrary>)
    }

    fun getLibraries(callback: OnLibrariesLoadedCallback) = launch {
        callback.onLoad(manager.getLibraries())
    }

    fun editLibrary(
        current: SketchwareLibrary,
        new: SketchwareLibrary,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.editLibrary(current, new)
        callback?.onFinish()
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()
}