# Sketchware Projects Manager
The best library for interacting with Sketchware projects. Change project info, delete, copy, create project and so on.
## Implementation
### For Gradle on Groovy
Add it to root build.gradle:
```groovy
repositories {
	maven { url 'https://jitpack.io' }
}
```
After it add next:
```groovy
dependencies {
	implementation 'com.github.justneon33:Sketchware-Project-Manager:alpha-1.0'
}
```
### Kotlin DSL plugin
Or if you have Kotlin DSL plugin add it:
```kotlin
repositories {
    maven("https://jitpack.io")
}
```
After it add next:
```kotlin
dependencies {
	implementation("com.github.justneon33:Sketchware-Project-Manager:alpha-1.0")
}
```

More information on how it works read here: https://github.com/justneon33/Sketchware-Project-Manager/wiki

