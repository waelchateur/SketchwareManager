package io.sketchware.project.data.view

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.exceptions.ViewNotFoundException
import io.sketchware.models.sketchware.data.SketchwareWidgetRoot
import io.sketchware.utils.*
import io.sketchware.utils.SketchwareDataParser.getByTag
import io.sketchware.utils.SketchwareDataParser.toBlockDataModel
import java.io.File

/**
 * Responsible for the project UI (Layout and its children) and their state.
 * @param file File which usually located at ../.sketchware/data/%PROJECT_ID%/view
 * @throws SketchwareFileError if file doesn't exist or it isn't a file.
 */
class ViewManager(private val file: File) {
    private var decryptedString: String? = null

    init {
        if (!file.isFile)
            throw SketchwareFileError(file.path)
    }

    private suspend fun getDecryptedString(): String {
        if (decryptedString == null)
            decryptedString = String(FileEncryptor.decrypt(file.readFile()))
        return decryptedString ?: error("Decrypted string should be initialized")
    }

    /**
     * Get widgets by activity name.
     * @param viewName View Name (example: main)
     * @param widget specific widget to get (for example: fab).
     * @return list of [SketchwareWidgetRoot] or null if view / widget not found.
     */
    suspend fun getViewOrNull(viewName: String, widget: String? = null): List<SketchwareWidgetRoot>? {
        val fullName = if (widget != null) "$viewName.xml_$widget" else "$viewName.xml"
        return getDecryptedString().getByTag(fullName)?.toBlockDataModel()
            ?.values?.map { it.toModel() }
    }

    /**
     * Get widgets by activity name.
     * @param viewName View Name (example: main)
     * @param widget specific widget to get (for example: fab).
     * @return list of [SketchwareWidgetRoot] or throws [ViewNotFoundException] if view / widget not found.
     */
    @Throws(ViewNotFoundException::class)
    suspend fun getView(viewName: String, widget: String? = null) =
        getViewOrNull(viewName, widget) ?: throw ViewNotFoundException(file.path, viewName, widget)

    /**
     * Modifies a View by the name of the View and / or Widget.
     * @param viewName View Name (example: main).
     * @param widget specific widget to get in the [viewName] (for example: fab).
     * @throws [ViewNotFoundException] if view not found.
     */
    @Throws(ViewNotFoundException::class)
    suspend fun editView(
        viewName: String,
        widget: String? = null,
        builder: MutableList<SketchwareWidgetRoot>.() -> Unit
    ) = saveView(
        viewName,
        widget,
        getView(viewName, widget).toMutableList().apply(builder)
    )

    /**
     * Modifies a View by the name of the View and / or Widget.
     * @param viewName View Name (example: main).
     * @param widget specific widget to get in the [viewName] (for example: fab).
     */
    suspend fun editView(
        viewName: String,
        widget: String? = null,
        widgets: List<SketchwareWidgetRoot>
    ) = saveView(viewName, widget, widgets)

    private suspend fun saveView(
        viewName: String,
        widget: String? = null,
        list: List<SketchwareWidgetRoot>
    ) {
        val name = "$viewName.xml".plus(
            if (widget == null)
                "" else "_$widget"
        )
        decryptedString = getDecryptedString().replaceOrInsertAtTop(
            "(@$name.*?)(?=@|\$)".toRegex(),
            if (list.isEmpty())
                throw IllegalArgumentException("list cannot be empty")
            else "@$name${BlockParser.toSaveableValue(list)}\n\n"
        )
        file.writeFile(FileEncryptor.encrypt(getDecryptedString().toByteArray()))
    }

}