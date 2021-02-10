package io.sketchware.project.data.resource

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchware.data.SketchwareProjectResource
import io.sketchware.utils.SketchwareDataParser.getByTag
import io.sketchware.utils.SketchwareDataParser.toBlockDataModel
import io.sketchware.utils.readFile
import io.sketchware.utils.toModel
import java.io.File

class ResourcesManager(private val file: File) {
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

    suspend fun getImages(): List<SketchwareProjectResource>? =
        getDecryptedString().getByTag("images")?.toBlockDataModel()
            ?.values?.map { it.toModel() }

    suspend fun getSounds(): List<SketchwareProjectResource>? =
        getDecryptedString().getByTag("sounds")?.toBlockDataModel()
            ?.values?.map { it.toModel() }

    suspend fun getFonts(): List<SketchwareProjectResource>? =
        getDecryptedString().getByTag("fonts")?.toBlockDataModel()
            ?.values?.map { it.toModel() }
}