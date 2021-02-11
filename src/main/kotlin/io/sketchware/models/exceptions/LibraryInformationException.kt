package io.sketchware.models.exceptions

class LibraryInformationException(libraryName: String, filePath: String) :
    Exception("No info found for library $libraryName at path $filePath.")