package io.sketchware.models.exceptions

class OnCreateNotFoundException(activity: String) : Exception("On create for activity $activity not found.")