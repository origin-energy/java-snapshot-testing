[![Build Status](https://img.shields.io/travis/origin-energy/java-snapshot-testing.svg)](https://travis-ci.org/origin-energy/java-snapshot-testing)

## READ FIRST
These docs are for the latest `.SNAPSHOT` version published to maven central.
Select the branch `release/X.X.X` matching your maven dependency to get correct documentation for your version.

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
- Can be difficult to troll though large snapshot changes
- Does not document the business rules the way a Unit test would

A Snapshot test does not assert Java types. You can continue doing that with any other testing framework.

## Installation [Maven](https://search.maven.org/search?q=java-snapshot-testing)

We currently support:
- [JUnit4](https://search.maven.org/search?q=a:java-snapshot-testing-junit4)
- [JUnit5](https://search.maven.org/search?q=a:java-snapshot-testing-junit5)
- [Spock](https://search.maven.org/search?q=a:java-snapshot-testing-spock)

However, any JVM testing framework should work if you correctly implement the `SnapshotConfig` interface and pass it into the `start()` method.

## How does it work
1. When a test runs for the first time a `.snap` file is created in a `__snapshots__` subdirectory
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
import au.com.origin.snapshots.junit5.SnapshotExtension;
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
import au.com.origin.snapshots.junit4.SnapshotClassRule;
import au.com.origin.snapshots.junit4.SnapshotRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class SnapshotRuleUsedTest {

    // Ensure you instantiate these rules
    @ClassRule public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();
    @Rule public SnapshotRule snapshotRule = new SnapshotRule();

    @Test
    public void shouldUseExtension() {
        // Verify your snapshot
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot();
    }

    @Test
    public void shouldUseExtensionAgain() {
        SnapshotMatcher.expect("Hello Wolrd Again").toMatchSnapshot();
    }
}
```

## Spock
```groovy
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

// Ensure you enable snapshot testing support
@EnableSnapshots
class SpockExtensionUsedSpec extends Specification {
    def "Should use extension"() {
        when:
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot()

        then:
        true
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
In cases where the same test runs multiple times with different parameters you need to set the `scenario` and it must be unique for each run

```java
SnapshotMatcher.expect(something).scenario(params).toMatchSnapshot();
```

## Scenario Example
```groovy
@EnableSnapshots
class MySpec extends Specification {

    def 'Convert #scenario to uppercase'() {
        when: 'I convert to uppercase'
        def result = MyUtility.toUpperCase(value)
        then: 'Should convert letters to uppercase'
        // Check you snapshot against your output using a unique scenario
        expect(uppercase).scenario(scenario).toMatchSnapshot()
        where:
        scenario | value
        'letter' | 'a'
        'number' | '1'
    }
}
```

## Supplying a custom SnapshotSerializer
The serializer determines how a class gets converted into a string.

Currently, we support two different serializers

| Serializer                 | Description                                                                           |
|----------------------------|---------------------------------------------------------------------------------------|
| ToStringSnapshotSerializer | uses the toString() method                                                            | 
| JacksonSnapshotSerializer  | uses [jackson](https://github.com/FasterXML/jackson) to convert a class to a snapshot |

Serializers are pluggable, so you can write you own by implementing the `SnapshotSerializer` interface.

There are three ways to override the Serializer and are resolved in the following order.
- `@UseCustomSerializer` (method level)
- `@UseCustomSerializer` (class level)
- `@UseCustomConfig` (class level -`getSerializer()` method)
- configured `SnapshotConfig` default for your test framework

```
@ExtendWith(SnapshotExtension.class)
@UseSnapshotSerializer(ToStringSnapshotSerializer.class)
public class SnapshotExtensionUsedTest {

    @UseSnapshotSerializer(JacksonSnapshotSerializer.class)
    @Test
    public void test1() { 
        // This will use the method level JacksonSnapshotSerializer to marshal into JSON
    }

    @Test
    public void test1() {
        // This will use the class level ToStringSnapshotSerializer
    }
}
```

## Supplying a custom SnapshotConfig
You can override the snapshot configuration easily using the `@UseSnapshotConfig` annotation

JUnit5 Example
```java
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
@UseSnapshotConfig(HibernateSnapshotConfig.class) // apply your custom snapshot configuration to this test class
public class SnapshotExtensionUsedTest {

    @Test
    public void myTest() {
        // ...
    }
}
```

## ~~Updating all snapshots and generating a new baseline~~ (Issue #15)
Often - after analysing each snapshot an verifying it is correct - you will need to generate a new baseline for the snapshots.

Instead of deleting or manually modifying each snapshot you can pass `-PupdateSnapshot="pattern` which is equivalent to the `--updateUnapshot` flag in Jest

This will update all snapshots containing the text passed as the value

# Configuring Serialization
Sometimes the default serialization doesn't work for you. An example is Hibernate serialization where you get infinite recursion on Lists/Sets.

You can supply any serializer you like Gson, Jackson or something else by overriding the `getSerializer()` method.

For example, here is a JUnit4 configuration that will exclude the rendering of Lists without changing the source code to include @JsonIgnore.
This is good because you shouldn't need to add annotations to your source code for testing purposes only.

```java
public class HibernateSnapshotConfig extends JUnit4Config {

    @Override
    public Function<Object[], String> getSerializer() {
        JacksonSerializer jacksonSerializer = new JacksonSerializer();

        jacksonSerializer.configure(objectMapper -> {
            // Ignore Hibernate Lists & Sets to prevent infinite recursion
            objectMapper.addMixIn(List.class, IgnoreTypeMixin.class);
            objectMapper.addMixIn(Set.class, IgnoreTypeMixin.class);

            // Ignore Fields that Hibernate generates for us automatically, and thus are not reproducible between runs
            objectMapper.addMixIn(BaseEntity.class, IgnoreHibernateEntityFields.class);
        });

        return jacksonSerializer.getSerializer();
    }

    @JsonIgnoreType
    class IgnoreTypeMixin {}

    abstract class IgnoreHibernateEntityFields {
        @JsonIgnore
        abstract Long getId();

        @JsonIgnore
        abstract Instant getCreatedDate();

        @JsonIgnore
        abstract Instant getLastModifiedDate();
    }
}
```

# Contributing

see `CONTRIBUTING.md`
