package io.sketchware.project.data.file

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchware.data.BlockDataModel
import io.sketchware.models.sketchware.data.SketchwareDataFile
import io.sketchware.utils.*
import io.sketchware.utils.SketchwareDataParser.getByTag
import io.sketchware.utils.SketchwareDataParser.toBlockDataModel
import java.io.File

class FileManager(private val file: File) {
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

    suspend fun getActivities(): List<SketchwareDataFile>? =
        getDecryptedString().getByTag("activity")?.toBlockDataModel()
            ?.values?.map { it.toModel() }

    suspend fun getCustomViews(): List<SketchwareDataFile>? =
        getDecryptedString().getByTag("customview")?.toBlockDataModel()
            ?.values?.map { it.toModel() }

    private suspend fun save(title: String, list: List<SketchwareDataFile>) {
        decryptedString = getDecryptedString().replaceOrInsertAtTop(
            "(@$title.*?)(?=@|\$)".toRegex(),
            if (list.isEmpty())
                ""
            else "@$title${BlockParser.toSaveableValue(list)}\n"
        )
        file.writeFile(FileEncryptor.encrypt(getDecryptedString().toByteArray()))
        this.decryptedString = null
    }

    /**
     * Adds activity.
     * @param activity data about activity.
     */
    suspend fun addActivity(activity: SketchwareDataFile) {
        val list = getActivities()?.toMutableList() ?: mutableListOf()
        list.add(activity)
        save("activity", list)
    }

    /**
     * Adds custom view.
     * @param customView data about custom view.
     */
    suspend fun addCustomView(customView: SketchwareDataFile) {
        val list = getCustomViews()?.toMutableList() ?: mutableListOf()
        list.add(customView)
        save("customview", list)
    }

}