package io.sketchware.models.exceptions

class SketchwareProConfigNotFoundException(path: String) : Exception("Sketchware Pro config not found at $path.")