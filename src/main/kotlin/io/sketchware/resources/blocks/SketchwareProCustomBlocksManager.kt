package io.sketchware.resources.blocks

import io.sketchware.models.sketchwarepro.resources.*
import io.sketchware.utils.*
import java.io.File

class SketchwareProCustomBlocksManager(
    private val blockFile: File,
    private val paletteFile: File
) {

    /**
     * Get list of custom blocks.
     * If files isn't exist or it just empty, returns empty list.
     */
    suspend fun getCustomBlocks(): List<CustomBlockGroup> {
        val customBlocks = blockFile.readFileOrNull()?.let {
            String(it).serializeOrNull<List<CustomBlock>>()
        } ?: listOf()

        val palettes = paletteFile.readFileOrNull()?.let {
            String(it).serializeOrNull<List<Palette>>()
        } ?: listOf()

        return palettes.mapIndexed { index, palette ->
            val id = index + 9 // The first group of blocks has a digital identifier with the number 9.
            CustomBlockGroup(
                id,
                palette.name,
                palette.color,
                customBlocks.filter { it.palette.toInt() == id }
            )
        }
    }

    /**
     * Removes group of blocks.
     * @param groupId Custom block group id.
     */
    suspend fun removeGroup(groupId: Int) {
        saveBlocks(
            getCustomBlocks().toMutableList().also {
                it.removeIf { group ->
                    group.groupId == groupId
                }
            }
        )
    }

    /**
     * Removes block from the resources.
     * @param groupId Custom block group id.
     * @param name Custom block name.
     */
    suspend fun removeBlock(groupId: Int, name: String) {
        val list = getCustomBlocks().toMutableList()
        val group = list.first { it.groupId == groupId }
        val newBlocks = group.blocks.toMutableList().apply {
            removeIf { it.palette.toInt() == groupId && it.name == name }
        }
        saveBlocks(list.apply {
            set(indexOf(group), group.copy(blocks = newBlocks))
        })
    }

    /**
     * Adds block to the resources.
     * @param group group to add.
     */
    suspend fun addBlocksGroup(group: CustomBlockGroup) {
        saveBlocks(
            getCustomBlocks().toMutableList().also {
                it.add(group)
            }
        )
    }

    suspend fun editBlocksGroup(groupId: Int, builder: CustomBlockGroup.() -> Unit) = saveBlocks(
        getCustomBlocks().toMutableList().apply {
            val group = first { it.groupId == groupId }
            set(indexOf(group), group.apply(builder))
        }
    )

    /**
     * Adds block to specific group.
     * @param groupId group id to which block will be added.
     * @param block block to add.
     */
    suspend fun addBlockToGroup(groupId: Int, block: CustomBlock) {
        val list = getCustomBlocks().toMutableList()
        val group = list.first { it.groupId == groupId }
        val newBlocks = group.blocks.toMutableList().apply {
            add(block)
        }
        saveBlocks(list.apply {
            set(indexOf(group), group.copy(blocks = newBlocks))
        })
    }

    private suspend fun saveBlocks(list: List<CustomBlockGroup>) {
        val allBlocks = mutableListOf<CustomBlock>()
        val allPalettes = mutableListOf<Palette>()
        list.forEach {
            allBlocks.addAll(it.blocks)
            allPalettes.add(Palette(it.hexColor, it.name))
        }
        blockFile.writeFile(allBlocks.toJson().toByteArray())
        paletteFile.writeFile(allPalettes.toJson().toByteArray())
    }

}