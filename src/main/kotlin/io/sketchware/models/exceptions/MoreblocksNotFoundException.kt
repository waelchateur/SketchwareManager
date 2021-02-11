package io.sketchware.models.exceptions

class MoreblocksNotFoundException(activity: String):
    Exception("Moreblocks not found for activity $activity. Does your activity exist?")