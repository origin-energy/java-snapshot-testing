[![Build Status](https://img.shields.io/travis/origin-energy/java-snapshot-testing.svg)](https://travis-ci.org/origin-energy/java-snapshot-testing)

## Java Snapshot Testing
- Inspired by [facebook's Jest framework](https://facebook.github.io/jest/docs/en/snapshot-testing.html)
- Fork of [json-snapshot.github.io](https://github.com/json-snapshot/json-snapshot.github.io)

# Jest Snapshot Testing for Java
The aim ot this project is to port Jest Snapshot testing for java project.

## Advantages of Snapshot Testing
It's useful for deterministic tests. That is, running the same tests multiple times on a component that has not changed 
should produce the same results every time. You're responsible for making sure your generated snapshots do not include 
platform specific or other non-deterministic data. 

- Great for testing JSON interfaces ensuring you don't break clients
- Fast and easy to test
- Will test implicitly test areas of your code you did not think about
- Great of testing dynamic objects

## Disadvantages of Snapshot Testing
- Does not give great insight to why the snapshot failed
- Can be difficult to to troll though large snapshot changes
- Does not document the business rules the way a Unit test would

A Snapshot test does not assert Java types. You can continue doing that with any other testing framework.

## Installation [Maven](https://mvnrepository.com/artifact/au.com.origin/java-snapshot-testing/1.0.0)

Maven
```xml
<dependency>
    <groupId>io.github.origin-energy</groupId>
    <artifactId>java-snapshot-testing</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

Gradle
```groovy
testCompile 'au.com.origin:java-snapshot-testing-FRAMEWORK:1.0.0'
```

## What is a Snapshot
```json
FIXME
```

# Framework Integration

## JUnit4
```java
FIXME
```

## JUnit5
```java
FIXME
```

## Spock
```groovy
FIXME
```

## Custom Configuration
If the default libraries don't work for you - you can implement your own

```java
public class MyCustomConfig implements SnapshotConfig {
    ... your custom implementation
}
```