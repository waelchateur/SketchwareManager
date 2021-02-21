package io.sketchware.models.sketchware

import io.sketchware.utils.StringNumberSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class with data about the project name, id, settings for the theme, etc.
 * It serializes usually from ../.sketchware/mysc/list/[projectId]/project
 */
@Serializable
data class ProjectConfig(
    /**
     * The version of Sketchware on which the project was last modified.
     */
    @SerialName("sketchware_ver")
    var sketchwareVersion: Double,
    /**
     * Unique digital identifier for the project.
     */
    @SerialName("sc_id")
    @Serializable(StringNumberSerializer::class)
    var projectId: Int,
    /**
     * Project's package name (example: com.android.test)
     */
    @SerialName("my_sc_pkg_name")
    var packageName: String,
    @SerialName("my_app_name")
    /**
     * Project's app name
     */
    var appName: String,
    /**
     * Project's app version code (Used to indicate new versions.)
     */
    @SerialName("sc_ver_code")
    var appVersionCode: String,
    @SerialName("color_control_highlight")
    var colorControlHighLight: Double,
    @SerialName("color_primary")
    var colorPrimary: Double,
    @SerialName("color_accent")
    var colorAccent: Double,
    @SerialName("my_sc_reg_dt")
    var projectCreationDate: String,
    @SerialName("color_primary_dark")
    var colorPrimaryDark: Double,
    @SerialName("color_control_normal")
    var colorControlNormal: Double,
    @SerialName("sc_ver_name")
    var appVersionName: String,
    /**
     * Project name (Not to be confused with [appName].)
     */
    @SerialName("my_ws_name")
    var projectName: String,
    /**
     * Is a custom icon installed.
     */
    @SerialName("custom_icon")
    var customIcon: Boolean
)