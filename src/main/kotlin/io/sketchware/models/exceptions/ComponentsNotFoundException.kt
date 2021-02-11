package io.sketchware.models.exceptions

class ComponentsNotFoundException(activity: String):
    Exception("Components not found for activity $activity. Does your activity exist?")