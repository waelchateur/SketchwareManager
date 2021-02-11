package io.sketchware.models.exceptions

class ActivitiesNotFoundException(filePath: String):
        Exception("Activities not found. Most likely, the project is broken or you specified the file path incorrectly." +
                " Path: $filePath.")