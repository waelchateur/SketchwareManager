package io.sketchware.java.wrapper.collections

import io.sketchware.collections.SketchwareCollection
import io.sketchware.collections.models.CollectionItem
import io.sketchware.java.wrapper.common.OnActionFinishedCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class SketchwareCollection(file: File, dataFolder: File = file.parentFile) : CoroutineScope {

    private val manager = SketchwareCollection(file, dataFolder)

    fun interface CollectionLoadedCallback {
        fun onLoad(list: List<CollectionItem>)
    }

    fun getCollection(callback: CollectionLoadedCallback) = launch {
        callback.onLoad(manager.getCollection())
    }

    fun getFileByName(name: String) = manager.getFileByName(name)

    fun addItem(collectionItem: CollectionItem, callback: OnActionFinishedCallback? = null) = launch {
        manager.addItem(collectionItem)
        callback?.onFinish()
    }

    fun removeItem(collectionItem: CollectionItem, callback: OnActionFinishedCallback? = null) = launch {
        manager.removeItem(collectionItem)
        callback?.onFinish()
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()
}