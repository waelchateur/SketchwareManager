package io.sketchware.resources.menu

import io.sketchware.models.sketchwarepro.resources.BlockInputMenu
import io.sketchware.models.sketchwarepro.resources.CustomMenu
import io.sketchware.models.sketchwarepro.resources.MenuData
import io.sketchware.utils.readFileOrNull
import io.sketchware.utils.serializeOrNull
import io.sketchware.utils.toJson
import io.sketchware.utils.writeFile
import java.io.File

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

    suspend fun editMenu(id: String, builder: CustomMenu.() -> Unit) = editMenu(
        id, getCustomMenus().toMutableList().first { it.id == id }.apply(builder)
    )

    suspend fun editMenu(id: String, menu: CustomMenu) = saveCustomMenus(
        getCustomMenus().toMutableList().apply {
            val oldMenu = first { it.id == id }
            set(indexOf(oldMenu), menu)
        }
    )

    private suspend fun saveCustomMenus(list: List<CustomMenu>) {
        menuBlockFile.writeFile(list.toJson().toByteArray())
    }

}