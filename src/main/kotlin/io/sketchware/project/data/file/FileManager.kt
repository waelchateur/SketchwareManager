package io.sketchware.project.data.file

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.ActivitiesNotFoundException
import io.sketchware.models.exceptions.CustomViewsNotFoundException
import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchware.data.SketchwareDataFile
import io.sketchware.utils.*
import io.sketchware.utils.SketchwareDataParser.getByTag
import io.sketchware.utils.SketchwareDataParser.toBlockDataModel
import java.io.File

/**
 * The class is responsible for working with activities, customviews in the project
 * which is usually found along the path ../.sketchware/data/%PROJECT_ID%/file.
 * @param [file] file which is usually found along the path ../.sketchware/data/%PROJECT_ID%/file.
 * @throws [SketchwareFileError] if file doesn't exist or it isn't a file.
 */
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

    /**
     * Gets list of activities in specific project.
     * @return list of [SketchwareDataFile] or exception
     * if activities not found (Or rather when the project is broken or something like that,
     * since @activity and @customview are always created when the project is created).
     * @throws [ActivitiesNotFoundException] if project is broken or path invalid.
     */
    @Throws(ActivitiesNotFoundException::class)
    suspend fun getActivities(): List<SketchwareDataFile> =
        getDecryptedString().getByTag("activity")?.toBlockDataModel()
            ?.values?.map { it.toModel() } ?: throw ActivitiesNotFoundException(file.path)

    /**
     * Gets list of custom views in specific project.
     * @return list of [SketchwareDataFile] or exception if activities not found (Or rather
     * when the project is broken or something like that, since @activity and @customview
     * are always created when the project is created)
     */
    suspend fun getCustomViews(): List<SketchwareDataFile> =
        getDecryptedString().getByTag("customview")?.toBlockDataModel()
            ?.values?.map { it.toModel() } ?: throw CustomViewsNotFoundException(file.path)

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
        val list = getActivities().toMutableList()
        list.add(activity)
        save("activity", list)
    }

    /**
     * Adds custom view.
     * @param customView data about custom view.
     */
    suspend fun addCustomView(customView: SketchwareDataFile) {
        val list = getCustomViews().toMutableList()
        list.add(customView)
        save("customview", list)
    }

}