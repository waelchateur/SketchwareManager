package io.sketchware.java.wrapper.project.data.view

import io.sketchware.java.wrapper.common.OnActionFinishedCallback
import io.sketchware.models.sketchware.data.SketchwareWidgetRoot
import io.sketchware.project.data.view.ViewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ViewManager(file: File) : CoroutineScope {
    private val manager = ViewManager(file)

    fun interface OnViewLoadedCallback {
        fun onLoad(widgets: List<SketchwareWidgetRoot>?)
    }

    fun getView(
        viewName: String,
        widget: String? = null,
        callback: OnViewLoadedCallback
    ) = launch {
        callback.onLoad(manager.getView(viewName, widget))
    }

    fun editView(
        viewName: String,
        widget: String? = null,
        widgets: List<SketchwareWidgetRoot>,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.editView(viewName, widget, widgets)
        callback?.onFinish()
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()
}