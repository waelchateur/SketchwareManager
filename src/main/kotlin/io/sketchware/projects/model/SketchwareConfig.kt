package io.sketchware.projects.model

import java.io.File

data class SketchwareConfig(
    var mysc: File? = null,
    var myscList: File? = null,
    /** folder with info about view,logic */
    var bak: File? = null,
    /** folder with info about files, view, logic, resources, libraries */
    var data: File? = null,
    var resources: SketchwareResourcesPaths? = null
) {
    var myscPath: String
        get() = mysc?.path ?: ""
        set(value) {
            mysc = File(value)
        }

    var myscListPath
        get() = myscList?.path ?: ""
        set(value) {
            myscList = File(value)
        }

    var bakPath: String
        get() = bak?.path ?: ""
        set(value) {
            bak = File(value)
        }

    var dataPath: String
        get() = data?.path ?: ""
        set(value) {
            data = File(value)
        }

    constructor(basePath: String, projectId: Int):
            this(
                File("$basePath/mysc/$projectId"),
                File("$basePath/mysc/list/$projectId/project"),
                File("$basePath/bak/$projectId"),
                File("$basePath/data/$projectId"),
                SketchwareResourcesPaths(
                    "$basePath/resources/icons/$projectId",
                    "$basePath/resources/images/$projectId",
                    "$basePath/resources/fonts/$projectId",
                    "$basePath/resources/sounds/$projectId"
                )
            )
}