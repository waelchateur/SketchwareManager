package io.sketchware.models.exceptions

class ResourcesNotFoundException(filePath: String, tag: String) :
    Exception("${tag.capitalize()} resources not found. Does your project exist?")