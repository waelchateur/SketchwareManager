# 📂 Sketchware Projects Manager
The best library for interacting with Sketchware projects. Change project info, delete, copy, create project and so on.
## 🛠 Implementation
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
	implementation 'com.github.justneon33:Sketchware-Project-Manager:stable-1.2'
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
	implementation("com.github.justneon33:Sketchware-Project-Manager:stable-1.2")
}
```
## 📜 Instructions
We have wiki where a lot useful info - https://github.com/justneon33/Sketchware-Project-Manager/wiki.
Also if wouldn't find something about your question create issue.
##  ❗ There also a new versions to check:
### Dev version 🧪
Full rewrite to coroutine-based library for managing sketchware projects, collections and etc.
[Check on dev branch](https://github.com/y9neon/Sketchware-Project-Manager/tree/dev).
