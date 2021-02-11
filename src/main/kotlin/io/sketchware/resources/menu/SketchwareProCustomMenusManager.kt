package io.sketchware.resources.menu

import io.sketchware.models.sketchwarepro.resources.BlockInputMenu
import io.sketchware.models.sketchwarepro.resources.CustomMenu
import io.sketchware.models.sketchwarepro.resources.MenuData
import io.sketchware.utils.readFileOrNull
import io.sketchware.utils.serializeOrNull
import io.sketchware.utils.toJson
import io.sketchware.utils.writeFile
import java.io.File

/**
 * Responsible for working with Sketchware Pro custom menus.
 * @param menuBlockFile File which usually located at ../.sketchware/resources/block/Menu Block/block.json
 * @param menuDataFile File which usually located at ../.sketchware/resources/block/Menu Block/data.json
 */
class SketchwareProCustomMenusManager(
    private val menuBlockFile: File,
    private val menuDataFile: File
) {

    /**
     * @return list of custom menus.
     */
    suspend fun getCustomMenus(): List<CustomMenu> {
        val menus = menuBlockFile.readFileOrNull()?.let {
            String(it).serializeOrNull<List<BlockInputMenu>>()
        } ?: listOf()

        val menusData = menuDataFile.readFileOrNull()?.let {
            String(it).serializeOrNull<HashMap<String, List<MenuData>>>()
        } ?: hashMapOf()

        return menusData.keys.toList().map { key ->
            val menu = menusData[key]!!
            val values = menu.map { it.value.split("+") }.flatten()

            return@map CustomMenu(
                menus.map(BlockInputMenu::id).first(key::equals),
                menus.find { key == it.id }?.name
                    ?: error("Unexpected error while getting custom menu. Name is unspecified."),
                menu[0].title,
                values
            )
        }
    }

    /**
     * Adds custom menu.
     * @param menu menu to add
     */
    suspend fun addCustomMenu(menu: CustomMenu) = saveCustomMenus(
        getCustomMenus().toMutableList().also { it.add(menu) }
    )

    /**
     * Removes custom menu by id.
     * @param id menu's string id.
     */
    suspend fun removeMenuById(id: String) = saveCustomMenus(
        getCustomMenus().toMutableList().also { menus ->
            menus.removeIf { it.id == id }
        }
    )

    /**
     * Edits custom menu.
     * @param id menu string id.
     * @param builder Lambda with [CustomMenu] in context to edit already exists menu data.
     */
    suspend fun editMenu(id: String, builder: CustomMenu.() -> Unit) = editMenu(
        id, getCustomMenus().toMutableList().first { it.id == id }.apply(builder)
    )

    /**
     * Edits custom menu.
     * @param id menu string id.
     * @param menu new menu data.
     */
    suspend fun editMenu(id: String, menu: CustomMenu) = saveCustomMenus(
        getCustomMenus().toMutableList().apply {
            val oldMenu = first { it.id == id }
            set(indexOf(oldMenu), menu)
        }
    )

    private suspend fun saveCustomMenus(list: List<CustomMenu>) {
        val blocks = mutableListOf<BlockInputMenu>()
        val dataList = mutableListOf<MutableMap<String, List<MenuData>>>()
        list.forEach {
            blocks.add(BlockInputMenu(it.id, it.name))
            val data = mutableMapOf<String, List<MenuData>>()
            data[it.id] = listOf(MenuData(it.title, it.options.joinToString("+")))
            dataList.add(data)
        }
        menuBlockFile.writeFile(blocks.toJson().toByteArray())
        menuDataFile.writeFile(dataList.toJson().toByteArray())
    }

}