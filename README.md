[![Build Status](https://img.shields.io/travis/origin-energy/java-snapshot-testing.svg)](https://travis-ci.org/origin-energy/java-snapshot-testing)

## Java Snapshot Testing
- Inspired by [facebook's Jest framework](https://facebook.github.io/jest/docs/en/snapshot-testing.html)
- Fork of [json-snapshot.github.io](https://github.com/json-snapshot/json-snapshot.github.io)

# Jest Snapshot Testing for Java
The aim of this project is to port Jest Snapshot testing for jvm projects.

## Advantages of Snapshot Testing
It's useful for deterministic tests. That is, running the same tests multiple times on a component that has not changed 
should produce the same results every time. You're responsible for making sure your generated snapshots do not include 
platform specific or other non-deterministic data. 

- Great for testing JSON interfaces ensuring you don't break clients
- Fast and easy to test
- Will implicitly test areas of your code you did not think about
- Great of testing dynamic objects

## Disadvantages of Snapshot Testing
- Does not give great insight to why the snapshot failed
- Can be difficult to to troll though large snapshot changes
- Does not document the business rules the way a Unit test would

A Snapshot test does not assert Java types. You can continue doing that with any other testing framework.

## Installation [Maven](https://search.maven.org/search?q=java-snapshot-testing)

We currently support:
- [JUnit4](https://search.maven.org/search?q=a:java-snapshot-testing-junit4)
- [JUnit5](https://search.maven.org/search?q=a:java-snapshot-testing-junit5)
- [Spock](https://search.maven.org/search?q=a:java-snapshot-testing-spock)

However, any JVM testing framework should work if you correctly implement the `SnapshotConfig` interface and pass it into the `start()` method.

## How does it work
1. When a test is run for the first time a `.snap` file is created in a `__snapshots__` subdirectory
1. On subsequent test runs the `.snap` file is compared with the one produced by the test
1. If they don't match the test fails
1. It is then your job to decide if you have introduced a regression or intentionally changed the output
1. If you have introduced a regression you will need to fix your code
1. If you have intentionally changed the output you can manually modify the `.snap` file to make it pass or delete it and it will be generated again from scratch

## What is a Snapshot
A text (usually json) representation of your java object.

As an example
```text
com.example.ExampleTest.shouldExtractArgsFromFakeMethodWithComplexObject=[
  {
    "FakeObject.fakeMethodWithComplexObject": [
      {
        "arg0": {
          "id": "idMock"
        }
      }
    ]
  },
  {
    "FakeObject.fakeMethodWithComplexObject": [
      {
        "arg0": {
          "id": "idMock",
          "name": "nameMock"
        }
      }
    ]
  }
]
```

# Usage Examples
## JUnit 5
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Ensure you extend your test class with the SnapshotExtension
@ExtendWith(SnapshotExtension.class)
public class SnapshotExtensionUsedTest {

    @Test
    public void shouldUseExtension() {
        // Verify your snapshot
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot();
    }

    @Test
    public void exampleSnapshot() {
        SnapshotMatcher.expect("Hello Wolrd Again").toMatchSnapshot();
    }
}
```
## JUnit 4
```java
import org.junit.ClassRule;
import org.junit.Test;

public class SnapshotRuleUsedTest {

    // Ensure you instantiate a class rule
    @ClassRule
    public static SnapshotRule snapshotRule = new SnapshotRule();

    @Test
    public void exampleSnapshot() {
        // Verify your snapshot
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot();
    }
}
```

## Spock
```groovy
package specs
import static io.github.jsonSnapshot.SnapshotMatcher.expect
import io.github.jsonSnapshot.SnapshotMatcher
import io.github.jsonSnapshot.SpockConfig
import spock.lang.Specification

class MySpec extends Specification {

    def setupSpec() {
        // Start snapshot testing before any tests have run passing in the appropriate environment configuration
        SnapshotMatcher.start(new SpockConfig())
    }

    def cleanupSpec() {
        // Validate the snapshots after all tests have executed
        SnapshotMatcher.validateSnapshots()
    }

    def 'Convert #scenario to uppercase'() {
        when: 'I convert to uppercase'
        def result = MyUtility.toUpperCase(value)
        then: 'Should convert letters to uppercase'
        // Check you snapshot against your output
        expect(uppercase).scenario(scenario).toMatchSnapshot()
        where:
        scenario | value
        'letter' | 'a'
        'number' | '1'
    }
}
```
# Custom Framework
1. implement the interface `au.com.origin.snapshots.SnapshotConfig`
```java
public class MyCustomSnapshotConfig implements SnapshotConfig {
    // your custom implementation
}
```
1. Before all the tests in a single file execute
```java
  SnapshotMatcher.start(new MyCustomSnapshotConfig());
```
1. After all the tests in a single file execute
```java
  SnapshotMatcher.validateSnapshots();
```
1. In test methods setup your expectations (only one allowed per method)
```java
SnapshotMatcher.expect(something).toMatchSnapshot()
```

# Parameterized tests
In cases where the same test runs multiple times with different parameters you need to set the `scenario`

```java
SnapshotMatcher.expect(something).scenario(params).toMatchSnapshot();
```