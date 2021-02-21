import org.jetbrains.kotlin.konan.properties.loadProperties


plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.10"
    `maven-publish`
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}

publishing {
    publications {
        create<MavenPublication>(name = project.name) {
            from(components["kotlin"])
        }
    }
}

val vcs = "https://github.com/y9neon/SketchwareManager"
val projectName = project.name

val localProperties = project.rootProject.file("local.properties")
    .takeIf(File::exists)
    ?.let(File::getAbsolutePath)
    ?.let(::loadProperties)

val libVersion = "dev-2.3.0"

allprojects {
    group = "io.sketchware"
    version = libVersion

    apply(plugin = "maven-publish")

    publishing {
        apply(plugin = "maven-publish")
        publications {
            create<MavenPublication>("Deploy") {
                groupId = group as String
                artifactId = "SketchwareManager"
                version = libVersion
            }
        }

        repositories {
            maven {
                name = "sketchware-api"
                url = uri(localProperties!!["serverURI"]!!)
                credentials {
                    username = (localProperties["username"] as String?)!!
                    password = (localProperties["password"] as String?)!!
                }
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}
