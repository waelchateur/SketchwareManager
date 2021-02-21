package io.sketchware.project.data.library

import io.sketchware.encryptor.FileEncryptor
import io.sketchware.models.exceptions.LibraryInformationException
import io.sketchware.models.exceptions.SketchwareFileError
import io.sketchware.models.sketchware.data.BlockDataModel
import io.sketchware.models.sketchware.data.SketchwareLibrary
import io.sketchware.utils.*
import java.io.File

/**
 * The class is responsible for managing the libraries of the project.
 * @param file File which which is usually found along the path ../.sketchware/data/%PROJECT_ID%/library.
 * @throws [SketchwareFileError] if [file] isn't a file.
 */
class LibraryManager(private val file: File) {
    private var list: List<BlockDataModel>? = null
    private var decryptedString: String? = null

    init {
        if (file.isDirectory)
            throw SketchwareFileError(file.path)
        if(!file.exists())
            decryptedString = ""
    }

    private suspend fun getDecryptedString(): String {
        if (decryptedString == null)
            decryptedString = String(FileEncryptor.decrypt(file.readFile()))
        return decryptedString ?: error("Decrypted string should be initialized")
    }

    private suspend fun getList(): List<BlockDataModel> {
        if (list == null)
            list = SketchwareDataParser.parseJsonBlocks(getDecryptedString())
        return list ?: error("List shouldn't be null")
    }

    /**
     * Gets list of [SketchwareLibrary] in specific project.
     * @throws [SketchwareLibrary] if library configured incorrectly.
     * @return list of [SketchwareLibrary] or exception.
     */
    suspend fun getLibraries(): List<SketchwareLibrary> {
        return getList().map {
            SketchwareLibrary(
                it.name, it.values.singleOrNull()?.toModel()
                    ?: throw LibraryInformationException(it.name, file.path)
            )
        }
    }

    /**
     * Edits specific library
     * @param name Name of the library
     */
    suspend fun editLibrary(name: String, builder: SketchwareLibrary.() -> Unit) {
        val lib = getLibraries().find { it.name == name }
            ?: throw NoSuchElementException("No library with name $name.")
        editLibrary(lib, lib.apply(builder))
    }

    suspend fun editLibrary(currentLib: SketchwareLibrary, builder: SketchwareLibrary.() -> Unit) {
        editLibrary(currentLib, currentLib.apply(builder))
    }

    suspend fun editLibrary(currentLib: SketchwareLibrary, newLib: SketchwareLibrary) {
        val libraries = getLibraries().toMutableList()

        val index = libraries.indexOf(currentLib)
        libraries[index] = newLib

        saveLibraries(libraries)
    }

    private suspend fun saveLibraries(list: List<SketchwareLibrary>) {
        val result = list.joinToString("") { library ->
            "@${library.name}\\n${library.information.toJson()}\n\n"
        }
        file.writeFile(FileEncryptor.encrypt(result.toByteArray()))
        this.decryptedString = result
        this.list = null
    }

}