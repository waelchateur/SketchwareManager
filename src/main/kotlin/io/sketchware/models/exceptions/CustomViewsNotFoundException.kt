package io.sketchware.models.exceptions

class CustomViewsNotFoundException(filePath: String):
    Exception("Custom views not found. Most likely, the project is broken or you specified the file path incorrectly." +
            " Path: $filePath.")