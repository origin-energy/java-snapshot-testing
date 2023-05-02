# Using and testing the latest SNAPSHOT (excuse the pun) from maven central

Gradle

```
repositories {
    // ...
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}

dependencies {
    // ...

    // Replace {FRAMEWORK} with you testing framework
    // Replace {X.X.X} with the version number from `/gradle.properties`
    testCompile "io.github.origin-energy:java-snapshot-testing-{FRAMEWORK}:{X.X.X}-SNAPSHOT"
}
```