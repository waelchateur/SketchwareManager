package io.sketchware.models.exceptions

import java.lang.Exception

class LibraryInformationException(libraryName: String, filePath: String):
        Exception("No info found for library $libraryName at path $filePath.")