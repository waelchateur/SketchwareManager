package io.sketchware.project.data.logic

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.sketchware.SketchwareBlock
import io.sketchware.models.sketchware.data.*
import io.sketchware.utils.*
import io.sketchware.utils.replaceOrInsertAtTop
import io.sketchware.utils.writeFile
import java.io.File

open class LogicManager(private val file: File) {
    private var list: List<BlockDataModel>? = null
    private var decryptedString: String? = null

    private suspend fun getDecryptedString(): String {
        if (decryptedString == null)
            decryptedString = String(FileEncryptor.decrypt(file.readFile()))
        return decryptedString ?: throw error("list shouldn't be null")
    }

    private suspend inline fun <reified T> getBlock(name: String): List<T>? {
        val result = "(?<=@)(${name.replace(".", "\\.")})(.*?)(?=\\n@|$)"
            .toRegex()
            .find(getDecryptedString())
        return if(result?.groups?.get(2) == null)
            null
        else BlockParser.parseAsArray(result.groups[2]!!.value)
    }

    private suspend fun getTextBlock(name: String): List<Pair<String, String>>? {
        val result = "(?<=@)($name)(.*?)(?=\\n@|$)".toRegex()
            .find(getDecryptedString())
        if(result?.groups?.get(2) == null)
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
        this.list = null
        decryptedString = null
    }

    /**
     * Get activity events
     * @param activity activity name (Example: MainActivity)
     */
    suspend fun getEvents(activity: String) =
        getBlock<SketchwareEvent>("$activity.java_events")

    /**
     * Get activity moreblocks
     * @param activity activity name (Example: MainActivity)
     */
    suspend fun getMoreblocks(activity: String) =
        getTextBlock("$activity.java_func")?.map { (name, data) ->
            SketchwareProjectMoreblock(name, data)
        }
    /**
     * Get components in specific activity.
     * @param activity activity name (Example: MainActivity)
     * @return List of SketchwareComponent.
     */
    suspend fun getComponents(activity: String): List<SketchwareComponent>? =
        getBlock("$activity.java_components")

    /**
     * Get variables in specific activity.
     * @param activity Activity name (example: MainActivity)
     */
    suspend fun getVariables(activity: String) =
        getTextBlock("$activity.java_var")?.map { (name, type) ->
            SketchwareVariable(name, type.toInt())
        }

    /**
     * Get logic of moreblock.
     * @return blocks in moreblock.
     */
    suspend fun getMoreblockLogic(activity: String, name: String): List<SketchwareBlock>? =
        getBlock("$activity.java_${name}_moreBlock")

    /**
     * Get logic of event.
     * @return blocks in event.
     */
    suspend fun getEventLogic(activity: String, targetId: String, eventName: String) =
        getBlock<SketchwareBlock>("$activity.java_${targetId}_$eventName")

    /**
     * Get blocks in onCreate. Sketchware doesn't mark it as event (wtf xdd),
     * that's why there is additional method.
     * @return blocks in onCreate
     */
    suspend fun getOnCreateLogic(activity: String) =
        getBlock<SketchwareBlock>("$activity.java_onCreate_initializeLogic")

    /**
     * Edit onCreate event.
     * @param activity Activity Name (example: MainActivity)
     */
    suspend fun editOnCreateLogic(
        activity: String,
        builder: ArrayList<SketchwareBlock>.() -> Unit
    ) = editLogic(activity, "onCreate_initializeLogic", builder)

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
        val components = ArrayList(getComponents(activity) ?: ArrayList())
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

    private suspend fun saveMoreblocks(activity: String, list: List<SketchwareProjectMoreblock>) =
        saveBlock("$activity.java_func", list.joinToString("\n") { "${it.name}:${it.data}" })

    /**
     * Add variable to specific activity.
     * @param activity Activity Name (example: MainActivity)
     * @param variable SketchwareVariable instance which contains data about variable
     */
    suspend fun addVariable(activity: String, variable: SketchwareVariable) {
        val variables = ArrayList(getVariables(activity) ?: ArrayList())
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
        moreblock: SketchwareProjectMoreblock,
        contentBlocks: List<SketchwareBlock>
    ) {
        val moreblocks = ArrayList(getMoreblocks(activity) ?: ArrayList())
        moreblocks.add(moreblock)
        saveMoreblocks(activity, moreblocks)
        saveMoreblockLogic(activity, moreblock.name, contentBlocks)
    }

    private suspend fun saveMoreblockLogic(activity: String, name: String, list: List<SketchwareBlock>) =
        saveLogic(activity, "$activity.java_${name}_moreBlock", list)

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
    suspend fun removeMoreblock(activity: String, moreblock: SketchwareProjectMoreblock): Boolean {
        val moreblocks = ArrayList(getMoreblocks(activity) ?: return false)
        removeLogic(activity, "${moreblock.name}_moreBlock")
        return moreblocks.remove(moreblock).also { if (it) saveMoreblocks(activity, moreblocks) }
    }

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

    private suspend fun removeLogic(activity: String, name: String) {
        decryptedString = Regex(
            "(?<=@)${activity}\\.java_$name.*?(?=\\n@|\$)", RegexOption.DOT_MATCHES_ALL
        ).replace(getDecryptedString(), "\n")
    }

}