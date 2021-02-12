package io.sketchware.utils

import io.sketchware.models.sketchwarepro.resources.CustomBlockGroup
import io.sketchware.models.sketchwarepro.resources.CustomMenu
import io.sketchware.resources.blocks.SketchwareProCustomBlocksManager
import io.sketchware.resources.menu.SketchwareProCustomMenusManager

internal object SketchwareCustomUtils {
    suspend fun insertBlocks(
        blockManager: SketchwareProCustomBlocksManager,
        list: List<CustomBlockGroup>
    ) {
        val customBlocks = blockManager.getCustomBlocks()
        list.forEach {
            if(!customBlocks.contains(it))
                blockManager.addBlocksGroup(it.copy(groupId = blockManager.nextFreeGroupId()))
        }
    }
    suspend fun insertMenus(
        menusManager: SketchwareProCustomMenusManager,
        list: List<CustomMenu>
    ) {
        val menus = menusManager.getCustomMenus()
        list.forEach {
            if(!menus.contains(it))
                menusManager.addCustomMenu(it)
        }
    }
}