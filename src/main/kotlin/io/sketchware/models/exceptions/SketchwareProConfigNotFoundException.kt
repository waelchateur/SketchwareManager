package io.sketchware.models.exceptions

import java.lang.Exception

class SketchwareProConfigNotFoundException(path: String) : Exception("Sketchware Pro config not found at $path.")