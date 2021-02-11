package io.sketchware.models.exceptions

class VariablesNotFoundException(activity: String) :
    Exception("Variables not found in activity $activity. Does your activity exist?")