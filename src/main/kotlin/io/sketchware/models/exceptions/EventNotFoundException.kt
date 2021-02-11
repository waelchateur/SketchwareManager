package io.sketchware.models.exceptions

class EventNotFoundException(activityName: String, eventName: String, targetId: String) :
    Exception("Event for name $eventName in activity $activityName for target $targetId not found.")