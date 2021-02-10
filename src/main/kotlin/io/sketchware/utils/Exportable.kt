package io.sketchware.utils

import java.io.File

/**
 * This class is responsible for exporting project elements / blocks / menus / etc. for its further transfer.
 */
class Exportable(
    private var items: MutableList<ExportableItem> = mutableListOf()
) {
    constructor(builder: MutableList<ExportableItem>.() -> Unit)
            : this(mutableListOf<ExportableItem>().apply(builder))

    suspend fun exportAsFolder(destination: File) = items.forEach {
        File(destination, "/${it.internalPath}/${it.fileName}").writeFile(it.value)
    }

    /**
     * Exports project as a zip.
     * @param tempFolder used to temporarily copy files to the desired structure.
     * @param zipFile The file to which the Zip will be saved.
     */
    suspend fun exportAsZip(tempFolder: File, zipFile: File) {
        exportAsFolder(tempFolder)
        ZipUtils.addFolderToZip(tempFolder, zipFile)
        deleteFiles(tempFolder)
    }

    operator fun plus(other: Exportable) {
        items.addAll(other.items)
    }

    companion object {
        /**
         * Generates list of items from folder.
         * @param folder The folder where the items will be saved to the [List].
         * @return [List] of [ExportableItem] which were obtained from the folder.
         */
        suspend fun getItemsFromFolder(folder: File): List<ExportableItem> {
            val output = mutableListOf<ExportableItem>()
            folder.getListFiles()?.forEach {
                if (it.isDirectory)
                    output.addAll(getItemsFromFolder(folder))
                else output.add(ExportableItem(folder.parentFile.name, folder.name, it.readBytes()))
            }
            return output
        }
    }

}

data class ExportableItem(
    val internalPath: String = "",
    val fileName: String,
    val value: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExportableItem

        if (internalPath != other.internalPath) return false
        if (fileName != other.fileName) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalPath.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}