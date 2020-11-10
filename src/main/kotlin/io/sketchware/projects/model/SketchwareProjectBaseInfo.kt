package io.sketchware.projects.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SketchwareProjectBaseInfo(
    @SerialName("sketchware_ver")
    val sketchwareVer: Double,
    @SerialName("sc_id")
    val scId: String,
    @SerialName("my_sc_pkg_name")
    val myScPkgName: String,
    @SerialName("my_app_name")
    val myAppName: String,
    @SerialName("sc_ver_code")
    val scVerCode: String,
    @SerialName("color_control_highlight")
    val colorControlHighLight: Double,
    @SerialName("color_primary")
    val colorPrimary: Double,
    @SerialName("color_accent")
    val colorAccent: Double,
    @SerialName("my_sc_reg_dt")
    val myScRegDt: String,
    @SerialName("color_primary_dark")
    val colorPrimaryDark: Double,
    @SerialName("color_control_normal")
    val colorControlNormal: Double,
    @SerialName("sc_ver_name")
    val scVerName: String,
    @SerialName("my_ws_name")
    val myWsName: String,
    @SerialName("custom_icon")
    val customIcon: Boolean
)