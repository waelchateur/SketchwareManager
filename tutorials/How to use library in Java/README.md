# How to use library in Java

Since the library was originally focused on Kotlin and used suspend functions
(which do not work in Java), you need to add one more dependency:

```groovy
dependencies {
    implementation 'io.sketchware:java-wrapper:alpha-2.2.6'
}
```

All functionality is implemented in almost the same style as in Kotlin. All that has changed is that you need to use
callbacks for almost every function. Well, also lambdas are simply replaced with types.

### Kotlin

```kotlin
suspend fun main() {
    val fileManager = FileManager(File(""))
    val activities = fileManager.getActivities()
    fileManager.addActivity(SketchwareDataFile(/* data */))
}
```

### In java with java-wrapper

```java
public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager(path);
        fileManager.getActivities(new FileManager.OnSketchwareDataFileLoadedCallback() {
            @Override
            public void onLoad(@Nullable List<SketchwareDataFile> data) {
                if(data != null)
                    System.out.println(data);
            }
        });
        fileManager.addActivity(
                new SketchwareDataFile(
                        /* data */
                ), new OnActionFinishedCallback() {
                    @Override
                    public void onFinish() {
                        // on added
                    }
                }
        );
    }
}
```

#### ⚠ Also, make sure you import the classes from the io.sketchware.java.wrapper package.
