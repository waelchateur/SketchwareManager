package io.sketchware.project.data.view

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchware.data.BlockDataModel
import io.sketchware.models.sketchware.data.SketchwareWidget
import io.sketchware.utils.*
import io.sketchware.utils.SketchwareDataParser.getByTag
import io.sketchware.utils.SketchwareDataParser.toBlockDataModel
import java.io.File

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
     */
    suspend fun getView(viewName: String, widget: String? = null): List<SketchwareWidget>? {
        val fullName = if(widget != null) "$viewName.xml_$widget" else "$viewName.xml"
        return getDecryptedString().getByTag(fullName)?.toBlockDataModel()
            ?.values?.map { it.toModel() }
    }

    suspend fun editView(
        viewName: String,
        widget: String? = null,
        builder: ArrayList<SketchwareWidget>.() -> Unit
    ) = saveView(viewName, widget, ArrayList(getView(viewName, widget) ?: ArrayList()).apply(builder))

    suspend fun editView(
        viewName: String,
        widget: String? = null,
        widgets: List<SketchwareWidget>
    ) = saveView(viewName, widget, widgets)

    private suspend fun saveView(
        viewName: String,
        widget: String? = null,
        list: List<SketchwareWidget>
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
        decryptedString = null
    }

}