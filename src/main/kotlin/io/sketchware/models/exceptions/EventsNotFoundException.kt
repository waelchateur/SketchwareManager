package io.sketchware.models.exceptions

class EventsNotFoundException(activity: String): Exception("Events not found for activity $activity.")