package io.sketchware.models.exceptions

class MoreblockNotFoundException(activity: String, moreblockName: String) :
    Exception("Moreblock $moreblockName not found in activity $activity. Does you moreblock and activity exist?")