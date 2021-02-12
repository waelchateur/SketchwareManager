package io.sketchware.models.sketchware

import java.io.File

abstract class ProjectResources

/**
 * Class with data about resource directories
 */
open class SketchwareProjectResources(
    var images: File,
    var icons: File,
    var fonts: File,
    var sounds: File
) : ProjectResources()
