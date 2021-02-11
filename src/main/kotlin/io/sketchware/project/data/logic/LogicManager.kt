package io.sketchware.project.data.logic

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.*
import io.sketchware.models.sketchware.SketchwareBlock
import io.sketchware.models.sketchware.data.SketchwareComponent
import io.sketchware.models.sketchware.data.SketchwareEvent
import io.sketchware.models.sketchware.data.SketchwareMoreblock
import io.sketchware.models.sketchware.data.SketchwareVariable
import io.sketchware.utils.*
import io.sketchware.utils.SketchwareDataParser.getByTag
import java.io.File

/**
 * The class is responsible for managing the logic of the project,
 * which is usually found along the path ../.sketchware/data/%PROJECT_ID%/logic.
 * It stores data about variables, moreblocks, events, components and their states / logic.
 * @param [file] File with project logic.
 * @throws [SketchwareFileError] if [file] doesn't exists or not a file.
 */
open class LogicManager(private val file: File) {
    private var decryptedString: String? = null

    init {
        if (!file.isFile || !file.exists())
            throw SketchwareFileError(file.path)
    }

    private suspend fun getDecryptedString(): String {
        if (decryptedString == null)
            decryptedString = String(FileEncryptor.decrypt(file.readFile()))
        return decryptedString ?: throw error("list shouldn't be null")
    }

    private suspend inline fun <reified T> getBlock(name: String): List<T>? =
        getDecryptedString().getByTag(name)?.let { BlockParser.parseAsArray(it) }

    private suspend fun getTextBlock(name: String): List<Pair<String, String>>? {
        val nameNormalized = name.replace(".", "\\.")
        val result = Regex("(?<=@)($nameNormalized)(.*?)(?=\\n@|$)", RegexOption.DOT_MATCHES_ALL)
            .find(getDecryptedString())
        if (result?.groups?.get(2) == null)
            return null
        return SketchwareDataParser.parseTextBlocks(result.groups[2]!!.value).map {
            Pair(it[0], it[1])
        }
    }

    private suspend inline fun <reified T> saveBlock(name: String, list: List<T>) =
        saveBlock(name, BlockParser.toSaveableValue(list))

    private suspend fun saveBlock(name: String, stringToSave: String) {
        decryptedString = getDecryptedString().replaceOrInsertAtTop(
            "(@${name.replace(".", "\\.")}.*?)(?=@|\$)".toRegex(),
            "@$name$stringToSave\n\n"
        )
        file.writeFile(FileEncryptor.encrypt(getDecryptedString().toByteArray()))
    }

    /**
     * Get activity events
     * @param activity activity name (Example: MainActivity)
     * @return List of events or null if no events in specific activity.
     */
    suspend fun getEventsOrNull(activity: String) =
        getBlock<SketchwareEvent>("$activity.java_events")


    /**
     * Get activity events
     * @param activity activity name (Example: MainActivity)
     * @return List of events or exception if no events in specific activity.
     * @throws [EventsNotFoundException] if no events in specific activity or activity doesn't exist.
     */
    suspend fun getEvents(activity: String) =
        getEventsOrNull(activity) ?: throw EventsNotFoundException(activity)

    /**
     * Get activity moreblocks
     * @param activity activity name (Example: MainActivity)
     * @return List of [SketchwareMoreblock] in specific activity or null
     * if activity / moreblocks doesn't exist.
     */
    suspend fun getMoreblocksOrNull(activity: String) =
        getTextBlock("$activity.java_func")?.map { (name, data) ->
            SketchwareMoreblock(name, data)
        }

    /**
     * Get activity moreblocks
     * @param activity activity name (Example: MainActivity)
     * @return List of [SketchwareMoreblock] in specific activity or exception
     * if activity / moreblocks doesn't exist.
     * @throws [MoreblocksNotFoundException] if activity / moreblocks doesn't exist.
     */
    @Throws(MoreblocksNotFoundException::class)
    suspend fun getMoreblocks(activity: String) =
        getMoreblocksOrNull(activity) ?: throw MoreblocksNotFoundException(activity)

    /**
     * Get components in specific activity.
     * @param activity activity name (Example: MainActivity)
     * @return List of SketchwareComponent or null if activity / components doesn't exist.
     */
    suspend fun getComponentsOrNull(activity: String): List<SketchwareComponent>? =
        getBlock("$activity.java_components")

    /**
     * Get components in specific activity.
     * @param activity activity name (Example: MainActivity)
     * @return List of SketchwareComponent or [ComponentsNotFoundException]
     * exception if activity / components doesn't exist.
     * @throws [ComponentsNotFoundException] if activity / components doesn't exist.
     */
    @Throws(ComponentsNotFoundException::class)
    suspend fun getComponents(activity: String) =
        getComponentsOrNull(activity) ?: throw ComponentsNotFoundException(activity)

    /**
     * Get variables in specific activity.
     * @param activity Activity name (example: MainActivity)
     * @return list of [SketchwareVariable] or null if activity / variables doesn't exist.
     */
    suspend fun getVariablesOrNull(activity: String) =
        getTextBlock("$activity.java_var")?.map { (type, name) ->
            SketchwareVariable(name, type.toInt())
        }

    /**
     * Get variables in specific activity.
     * @param activity Activity name (example: MainActivity)
     * @return list of [SketchwareVariable] or
     * [VariablesNotFoundException] exception if activity / variables doesn't exist.
     * @throws [VariablesNotFoundException] exception if activity / variables doesn't exist.
     */
    @Throws(VariablesNotFoundException::class)
    suspend fun getVariables(activity: String) =
        getVariablesOrNull(activity) ?: throw VariablesNotFoundException(activity)

    /**
     * Gets logic of moreblock.
     * @return blocks in moreblock or null if activity / moreblock doesn't exists.
     */
    suspend fun getMoreblockLogicOrNull(activity: String, name: String): List<SketchwareBlock>? =
        getBlock("$activity.java_${name}_moreBlock")

    /**
     * Gets logic of moreblock.
     * @return blocks in moreblock or null if activity / moreblock doesn't exists.
     * @throws MoreblocksNotFoundException if activity / moreblock doesn't exists.
     */
    @Throws(MoreblockNotFoundException::class)
    suspend fun getMoreblockLogic(activity: String, name: String) =
        getMoreblockLogicOrNull(activity, name) ?: throw MoreblockNotFoundException(activity, name)

    /**
     * Gets logic of event.
     * @return blocks in event.
     */
    suspend fun getEventLogicOrNull(activity: String, targetId: String, eventName: String) =
        getBlock<SketchwareBlock>("$activity.java_${targetId}_$eventName")

    /**
     * Gets logic of event.
     * @throws [EventNotFoundException] if event not found.
     * @return blocks in event.
     */
    @Throws(EventNotFoundException::class)
    suspend fun getEventLogic(activity: String, targetId: String, eventName: String) =
        getEventLogicOrNull(activity, targetId, eventName)
            ?: throw EventNotFoundException(activity, eventName, targetId)

    /**
     * Get blocks in onCreate. Sketchware doesn't mark it as event (wtf xdd),
     * that's why there is additional method.
     * @return blocks in onCreate or null if no onCreate found.
     */
    suspend fun getOnCreateLogicOrNull(activity: String) =
        getBlock<SketchwareBlock>("$activity.java_onCreate_initializeLogic")

    /**
     * Gets blocks in onCreate. Sketchware doesn't mark it as event (wtf xdd),
     * that's why there is additional method.
     * @throws [OnCreateNotFoundException] if onCreate not found for [activity].
     * @return blocks in onCreate
     */
    @Throws(OnCreateNotFoundException::class)
    suspend fun getOnCreateLogic(activity: String) =
        getOnCreateLogicOrNull(activity) ?: throw OnCreateNotFoundException(activity)

    /**
     * Edit onCreate event.
     * @param activity Activity Name (example: MainActivity)
     */
    suspend fun editOnCreateLogic(
        activity: String,
        builder: ArrayList<SketchwareBlock>.() -> Unit
    ) = editLogic(activity, "onCreate_initializeLogic", builder)

    suspend fun editOnCreateLogic(
        activity: String,
        list: List<SketchwareBlock>
    ) = saveLogic(activity, "onCreate_initializeLogic", list)

    /**
     * Removes event by event name and target id.
     * @param activity Activity Name (example: MainActivity)
     * @param eventName name of event
     * @param targetId Indicates the name of the widget.
     * @return true or false in depends on removing status (if isn't something changed, it returns false).
     */
    suspend fun removeEvent(activity: String, eventName: String, targetId: String): Boolean {
        val events = ArrayList(getEvents(activity) ?: return false)
        if (!events.removeIf
            { it.name == eventName && it.targetId == targetId }
        ) return false

        removeLogic(activity, "${targetId}_$eventName")
        saveEvents(activity, events)
        return true
    }

    /**
     * Save events. It will replace already exist events.
     * @param activity Activity Name (example: MainActivity)
     * @param list list of events
     */
    private suspend fun saveEvents(activity: String, list: List<SketchwareEvent>) =
        saveBlock("$activity.java_events", list)

    /**
     * Edit logic of some event.
     * @param activity Activity Name (example: MainActivity).
     * @param eventName Event Name (example: onClick).
     * @param targetId Target id (example: button1).
     */
    suspend fun editEventLogic(
        activity: String,
        eventName: String,
        targetId: String,
        builder: ArrayList<SketchwareBlock>.() -> Unit
    ) = editLogic(activity, "${targetId}_$eventName", builder)

    suspend fun editEventLogic(
        activity: String,
        eventName: String,
        targetId: String,
        list: List<SketchwareBlock>
    ) = saveLogic(activity, "${targetId}_$eventName", list)

    /**
     * Add event to project activity.
     * @param activity Activity name (example: MainActivity)
     * @param event Sketchware Event, be careful with adding
     */
    suspend fun addEvent(activity: String, event: SketchwareEvent, blocks: List<SketchwareBlock>) {
        val array = ArrayList(getEvents(activity) ?: ArrayList())
        array.add(event)
        saveEvents(activity, array)
        saveEventLogic(activity, event.name, event.targetId, blocks)
    }

    /**
     * Save components in readable for sketchware look.
     * @param activity Activity name (example: MainActivity)
     * @param list list of components to save
     */
    private suspend fun saveComponents(activity: String, list: List<SketchwareComponent>) =
        saveBlock("$activity.java_components", list)

    /**
     * Removes component by component id.
     * @param activity Activity Name (example: MainActivity)
     * @param componentId Component id.
     * @return true or false in depends on removing status (if isn't something changed, it returns false).
     */
    suspend fun removeComponent(activity: String, componentId: String): Boolean {
        val components = ArrayList(getComponents(activity) ?: return false)
        if (!components.removeIf { it.id == componentId }) return false
        saveComponents(activity, components)
        return true
    }

    /**
     * Add component to project activity.
     * @param activity Activity name (example: MainActivity)
     * @param component Sketchware Component
     */
    suspend fun addComponent(activity: String, component: SketchwareComponent) {
        val components = getComponentsOrNull(activity)?.toMutableList()
            ?: mutableListOf()
        components.add(component)
        saveComponents(activity, components)
    }

    /**
     * Save variables in readable for sketchware look.
     * @param activity Activity name (example: MainActivity)
     * @param list list of variables
     */
    private suspend fun saveVariables(activity: String, list: List<SketchwareVariable>) =
        saveBlock("$activity.java_var", list.joinToString("\n") { "${it.name}:${it.type}" })

    private suspend fun saveMoreblocks(activity: String, list: List<SketchwareMoreblock>) =
        saveBlock("$activity.java_func", list.joinToString("\n"))

    /**
     * Add variable to specific activity.
     * @param activity Activity Name (example: MainActivity)
     * @param variable SketchwareVariable instance which contains data about variable
     */
    suspend fun addVariable(activity: String, variable: SketchwareVariable) {
        val variables = getVariablesOrNull(activity)?.toMutableList() ?: mutableListOf()
        variables.add(variable)
        saveVariables(activity, variables)
    }

    /**
     * Remove variable.
     * @param activity Activity Name (example: MainActivity)
     * @param variable variable to delete.
     * @return delete status. If nothing deleted returns false.
     */
    suspend fun removeVariable(activity: String, variable: SketchwareVariable): Boolean {
        val variables = ArrayList(getVariables(activity) ?: return false)
        return variables.remove(variable).also { if (it) saveVariables(activity, variables) }
    }

    /**
     * Add moreblock to specific activity.
     * @param activity Activity Name (example: MainActivity)
     * @param moreblock SketchwareMoreblock Model with data about moreblock.
     * @param contentBlocks blocks in this moreblock.
     */
    suspend fun addMoreblock(
        activity: String,
        moreblock: SketchwareMoreblock,
        contentBlocks: List<SketchwareBlock>
    ) {
        val moreblocks = getMoreblocksOrNull(activity)?.toMutableList() ?: mutableListOf()
        moreblocks.add(moreblock)
        println("Activity name $activity")
        println("Moreblock name ${moreblock.name}")
        saveMoreblocks(activity, moreblocks)
        saveMoreblockLogic(activity, moreblock.name, contentBlocks)
    }

    private suspend fun saveMoreblockLogic(activity: String, name: String, list: List<SketchwareBlock>) =
        saveLogic(activity, "${name}_moreBlock", list)

    private suspend fun saveEventLogic(
        activity: String,
        eventName: String,
        targetId: String,
        list: List<SketchwareBlock>
    ) = saveLogic(activity, "${targetId}_$eventName", list)

    /**
     * Removes moreblock from the logic in specific activity.
     * @param activity Activity Name (example: MainActivity)
     * @param moreblock moreblock model with data about moreblock.
     */
    suspend fun removeMoreblock(activity: String, moreblock: SketchwareMoreblock): Boolean {
        val moreblocks = ArrayList(getMoreblocks(activity) ?: return false)
        removeLogic(activity, "${moreblock.name}_moreBlock")
        return moreblocks.remove(moreblock).also { if (it) saveMoreblocks(activity, moreblocks) }
    }

    /**
     * Saves logic for an event / moreblock.
     */
    private suspend fun saveLogic(activity: String, name: String, list: List<SketchwareBlock>) =
        saveBlock("$activity.java_$name", list)

    private suspend fun editLogic(
        activity: String,
        name: String,
        builder: ArrayList<SketchwareBlock>.() -> Unit
    ) {
        val list = ArrayList<SketchwareBlock>(
            getBlock("$activity.java_$name") ?: ArrayList()
        )
        saveLogic(activity, name, list.apply(builder))
    }

    /**
     * Edits moreblock logic.
     * @param activity Activity Name (example: MainActivity)
     * @param name Name of Moreblock
     */
    suspend fun editMoreblockLogic(
        activity: String,
        name: String,
        builder: ArrayList<SketchwareBlock>.() -> Unit
    ) = editLogic(activity, "${name}_moreBlock", builder)

    suspend fun editMoreblockLogic(
        activity: String,
        name: String,
        newLogic: List<SketchwareBlock>
    ) = saveLogic(activity, "${name}_moreBlock", newLogic)

    private suspend fun removeLogic(activity: String, name: String) {
        decryptedString = Regex(
            "(?<=@)${activity}\\.java_$name.*?(?=\\n@|\$)", RegexOption.DOT_MATCHES_ALL
        ).replace(getDecryptedString(), "\n")
    }

}