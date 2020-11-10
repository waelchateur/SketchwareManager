package io.sketchware.projects.manager.exception

class ProjectNotFoundException(projectId: Int, path: String) :
        RuntimeException("Project with id #$projectId in $path folder not found.")