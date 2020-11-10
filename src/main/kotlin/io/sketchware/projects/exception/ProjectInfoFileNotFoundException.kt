package io.sketchware.projects.manager.exception

class ProjectInfoFileNotFoundException(path: String) : RuntimeException("Project info file not found at $path.")