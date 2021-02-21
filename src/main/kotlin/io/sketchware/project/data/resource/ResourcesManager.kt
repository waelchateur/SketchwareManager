package io.sketchware.project.data.resource

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchware.data.SketchwareProjectResource
import io.sketchware.utils.SketchwareDataParser.getByTag
import io.sketchware.utils.SketchwareDataParser.toBlockDataModel
import io.sketchware.utils.readFile
import io.sketchware.utils.toModel
import java.io.File

/**
 * The class is responsible for working with project resources: images, fonts, sounds.
 * @param file File which is usually located at ../.sketchware/data/%PROJECT_ID/resource
 * @throws [SketchwareFileError] if file doesn't exist or it isn't a file.
 */
class ResourcesManager(private val file: File) {
    private var decryptedString: String? = null

    init {
        if (!file.isFile)
            throw SketchwareFileError(file.path)
        if (!file.exists())
            decryptedString = ""
    }

    private suspend fun getDecryptedString(): String {
        if (decryptedString == null)
            decryptedString = String(FileEncryptor.decrypt(file.readFile()))
        return decryptedString ?: error("Decrypted string should be initialized")
    }

    /**
     * Gets list of images as [SketchwareProjectResource] class.
     */
    suspend fun getImages(): List<SketchwareProjectResource>? =
        getDecryptedString().getByTag("images")?.toBlockDataModel()
            ?.values?.map { it.toModel() }

    /**
     * Gets list of sounds as [SketchwareProjectResource] class.
     */
    suspend fun getSounds(): List<SketchwareProjectResource>? =
        getDecryptedString().getByTag("sounds")?.toBlockDataModel()
            ?.values?.map { it.toModel() }

    /**
     * Gets list of fonts as [SketchwareProjectResource] class.
     */
    suspend fun getFonts(): List<SketchwareProjectResource>? =
        getDecryptedString().getByTag("fonts")?.toBlockDataModel()
            ?.values?.map { it.toModel() }
}