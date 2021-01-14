package io.sketchware.java.wrapper.project.data.logic

import io.sketchware.java.wrapper.common.OnActionFinishedCallback
import io.sketchware.models.sketchware.SketchwareBlock
import io.sketchware.models.sketchware.data.SketchwareComponent
import io.sketchware.models.sketchware.data.SketchwareEvent
import io.sketchware.models.sketchware.data.SketchwareMoreblock
import io.sketchware.models.sketchware.data.SketchwareVariable
import io.sketchware.project.data.logic.LogicManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class LogicManager(file: File) : CoroutineScope {
    private val manager = LogicManager(file)

    fun interface OnEventsLoadedCallback {
        fun onLoad(list: List<SketchwareEvent>?)
    }

    fun getEvents(
        activity: String,
        callback: OnEventsLoadedCallback
    ) = launch {
        callback.onLoad(manager.getEvents("activity"))
    }

    fun interface OnMoreblocksLoadedCallback {
        fun onLoad(list: List<SketchwareMoreblock>?)
    }

    fun getMoreblocks(
        activity: String,
        callback: OnMoreblocksLoadedCallback
    ) = launch {
        callback.onLoad(manager.getMoreblocks(activity))
    }

    fun interface OnComponentsLoadedCallback {
        fun onLoad(list: List<SketchwareComponent>?)
    }

    fun getComponents(
        activity: String,
        callback: OnComponentsLoadedCallback
    ) = launch {
        callback.onLoad(manager.getComponents(activity))
    }

    fun interface OnVariablesLoadedCallback {
        fun onLoad(list: List<SketchwareVariable>?)
    }

    fun getVariables(
        activity: String,
        callback: OnVariablesLoadedCallback
    ) = launch {
        callback.onLoad(manager.getVariables(activity))
    }

    fun interface OnLogicLoadedCallback {
        fun onLoad(blocks: List<SketchwareBlock>?)
    }

    fun getMoreblockLogic(
        activity: String,
        name: String,
        callback: OnLogicLoadedCallback
    ) = launch {
        callback.onLoad(manager.getMoreblockLogic(activity, name))
    }

    fun getEventLogic(
        activity: String,
        target: String,
        name: String,
        callback: OnLogicLoadedCallback
    ) = launch {
        callback.onLoad(manager.getEventLogic(activity, target, name))
    }

    fun getOnCreateLogic(
        activity: String,
        callback: OnLogicLoadedCallback
    ) = launch {
        callback.onLoad(manager.getOnCreateLogic(activity))
    }

    fun editOnCreateLogic(
        activity: String,
        newLogic: List<SketchwareBlock>,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.editOnCreateLogic(activity, newLogic)
        callback?.onFinish()
    }

    fun editEventLogic(
        activity: String,
        eventName: String,
        targetId: String,
        newLogic: List<SketchwareBlock>,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.editEventLogic(activity, eventName, targetId, newLogic)
        callback?.onFinish()
    }

    fun editMoreblockLogic(
        activity: String,
        name: String,
        newLogic: List<SketchwareBlock>,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.editMoreblockLogic(activity, name, newLogic)
        callback?.onFinish()
    }

    fun addEvent(
        activity: String,
        event: SketchwareEvent,
        logic: List<SketchwareBlock>,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.addEvent(activity, event, logic)
        callback?.onFinish()
    }

    fun removeEvent(
        activity: String,
        eventName: String,
        targetId: String,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.removeEvent(activity, eventName, targetId)
        callback?.onFinish()
    }

    fun addComponent(
        activity: String,
        component: SketchwareComponent,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.addComponent(activity, component)
        callback?.onFinish()
    }

    fun removeComponent(
        activity: String,
        componentId: String,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.removeComponent(activity, componentId)
        callback?.onFinish()
    }

    fun addMoreblock(
        activity: String,
        moreblock: SketchwareMoreblock,
        blocks: List<SketchwareBlock>,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.addMoreblock(activity, moreblock, blocks)
        callback?.onFinish()
    }

    fun removeMoreblock(
        activity: String,
        moreblock: SketchwareMoreblock,
        callback: OnActionFinishedCallback? = null
    ) = launch {
        manager.removeMoreblock(activity, moreblock)
        callback?.onFinish()
    }

    override val coroutineContext = GlobalScope.coroutineContext + Job()

}