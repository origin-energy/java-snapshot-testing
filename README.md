[![Build Status](https://github.com/origin-energy/java-snapshot-testing/workflows/build/badge.svg)](https://github.com/origin-energy/java-snapshot-testing/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.origin-energy/java-snapshot-testing-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.origin-energy/java-snapshot-testing-core)

# Java Snapshot Testing
- Inspired by [facebook's Jest framework](https://facebook.github.io/jest/docs/en/snapshot-testing.html)

🎉 3.X is live - parallel test support, easily override configuration via `snapshot.properties` yet *.snap format remains unchanged! This release will require some mechanical refactoring for those upgrading as `expect` is now injected as a test method parameter.

## The testing framework loved by ~~lazy~~ productive devs

- Tired of needing to `assertThat(foo).isEqualTo("bar")` again & again?
- Are you just wanting to ensure you don't break - for example - REST interfaces
- Are you manually saving text files for verification in your tests?

**Want a better way?**
Then java-snapshot-testing might just be what you are looking for!

## Quick Start (Junit5 + Gradle example)

1. Add test dependencies

```groovy
// In this case we are using the JUnit5 testing framework
testImplementation 'io.github.origin-energy:java-snapshot-testing-junit5:3.+'

// Many will want to serialize into JSON.  In this case you should also add the Jackson plugin
testImplementation 'io.github.origin-energy:java-snapshot-testing-plugin-jackson:3.+'
testImplementation 'com.fasterxml.jackson.core:jackson-core:2.11.3'
testImplementation 'com.fasterxml.jackson.core:jackson-databind:2.11.3'
testImplementation 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.11.3'
testImplementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3'

// slf4j logging implementation if you don't already have one
testImplementation("org.slf4j:slf4j-simple:2.0.0-alpha0")
```

2. Create `snapshot.properties` and configure your global settings. Be sure to set `output-dir` appropriately for your
   JVM language.

- /src/test/java/resources/snapshot.properties

 ```text
serializer=au.com.origin.snapshots.serializers.ToStringSnapshotSerializer
serializer.json=au.com.origin.snapshots.serializers.JacksonSnapshotSerializer
serializer.orderedJson=au.com.origin.snapshots.serializers.DeterministicJacksonSnapshotSerializer
comparator=au.com.origin.snapshots.comparators.PlainTextEqualsComparator
reporters=au.com.origin.snapshots.reporters.PlainTextSnapshotReporter
snapshot-dir=__snapshots__
output-dir=src/test/java
ci-env-var=CI
```

3. Enable snapshot testing and write your first test

```java
package au.com.origin.snapshots.docs
    
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import au.com.origin.snapshots.Expect;

@ExtendWith({SnapshotExtension.class})
public class MyFirstSnapshotTest {

  @Test
  public void toStringSerializationTest(Expect expect) {
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void jsonSerializationTest(Expect expect) {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "John Doe");
    map.put("age", 40);

    expect
        .serializer("json")
        .toMatchSnapshot(map);
  }
}
```

4. Run your test

Bingo - you should now see your snapshot in the `__snapshots__` folder created next to your test. Try
changing `"Hello World"` to `"Hello Universe"` and watch it fail with a `.debug` file.

```text
au.com.origin.snapshots.docs.MyFirstSnapshotTest.jsonSerializationTest=[
  {
    "age": 40,
    "name": "John Doe"
  }
]


au.com.origin.snapshots.docs.MyFirstSnapshotTest.toStringSerializationTest=[
Hello World
]
```

## Advantages of Snapshot Testing

- Great for testing JSON interfaces ensuring you don't break clients
- Fast and easy to test
- Will implicitly test areas of your code you did not think about
- Great of testing dynamic objects

You're responsible for making sure your generated snapshots do not include platform specific or other non-deterministic
data.

## Disadvantages of Snapshot Testing

- You need to ensure your test is deterministic for all fields (there are ways to ignore things like dates)
- Does not give great insight to why the snapshot failed
- Can be difficult to troll though large snapshot changes where you might only be interested in a small set of fields

## Installation [Maven](https://search.maven.org/search?q=java-snapshot-testing)

These docs are for the latest `-SNAPSHOT` version published to maven central. Select the tag `X.X.X` matching your maven
dependency to get correct documentation for your version.

Only if you want to integrate with an unsupported framework. [Show me how!](#using-an-unsupported-framework)

- [Core](https://search.maven.org/search?q=a:java-snapshot-testing-core)

We currently support:

- [JUnit4](https://search.maven.org/search?q=a:java-snapshot-testing-junit4)
- [JUnit5](https://search.maven.org/search?q=a:java-snapshot-testing-junit5)
- [Spock](https://search.maven.org/search?q=a:java-snapshot-testing-spock)

Plugins

- [Jackson for JSON serialization](https://search.maven.org/search?q=a:java-snapshot-testing-plugin-jackson)
    - You need jackson on your classpath (Gradle example)
      ```groovy
          // Required java-snapshot-testing peer dependencies
         testImplementation 'com.fasterxml.jackson.core:jackson-core:2.11.3'
         testImplementation 'com.fasterxml.jackson.core:jackson-databind:2.11.3'
         testImplementation 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.11.3'
         testImplementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3'
      ```

## How does it work?

1. When a test runs for the first time, a `.snap` file is created in a `__snapshots__` sub-directory
1. On subsequent test runs, the `.snap` file is compared with the one produced by the test
1. If they don't match, the test fails and a `.snap.debug` with the conflict is created
1. It is then your job to decide if you have introduced a regression or intentionally changed the output (Use your IDE
   file comparison tools to compare the two files or refer to the terminal output)
1. If you have introduced a regression you will need to fix your code
1. If you have intentionally changed the output you can manually modify the `.snap` file to make it pass or delete it
   and it will be generated again from scratch
1. Once you fix the test, the `*.snap.debug` file will get deleted

## What is a Snapshot?

A text representation of your java object (toString() or JSON).

**String snapshot example**

```java
expect("hello world","Hello world again!").toMatchSnapshot();
```

```text
au.com.example.company.HelloWorldTest.helloWorld=[
Hello world
Hello world again!
]
```

**JSON Snapshot Example**

```java
expect(userDto).serializer("json").toMatchSnapshot();
```

```text
au.com.example.company.UserEndpointTest.shouldReturnCustomerData=[
  {
    "id": "1",
    "firstName": "John",
    "lastName": "Smith",
    "age": 34
  }
]
```

# Usage Examples

## [JUnit 5](https://junit.org/junit5)

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import au.com.origin.snapshots.Expect;

// Ensure you extend your test class with the SnapshotExtension
@ExtendWith({SnapshotExtension.class})
public class JUnit5Example {

  @Test
  public void myTest1(Expect expect) {
    // Verify your snapshot
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void myTest2(Expect expect) {
    expect.toMatchSnapshot("Hello World Again");
  }
}
```

## [JUnit 4](https://junit.org/junit4)

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.junit4.SnapshotRunner;
import au.com.origin.snapshots.Expect;
import org.junit.Test;
import org.junit.runner.RunWith;

// Ensure you RunWith the SnapshotRunner
@RunWith(SnapshotRunner.class)
public class JUnit4Example {

  @Test
  public void myTest1(Expect expect) {
    // Verify your snapshot
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void myTest2(Expect expect) {
    expect.toMatchSnapshot("Hello World Again");
  }
}
```

## [Spock](http://spockframework.org/)

```groovy
package au.com.origin.snapshots.docs

import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification
import au.com.origin.snapshots.Expect;

// Ensure you enable snapshot testing support
@EnableSnapshots
class SpockExample extends Specification {
    def "Should use extension"(Expect expect) {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }
}
```

# Using an unsupported framework

This library is in no way restricted to JUnit4, Junit5 or Spock.

Any framework can support the library as long as it follows the following rules:

1. Before all the tests in a single file execute (once only)
    ```java
        SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new YourFrameworkSnapshotConfig(), testClass, failOnOrphans);
    ```
1. After all the tests in a single file execute (once only)
    ```java
      snapshotVerifier.validateSnapshots();
    ```
1. For each test class, setup your expectations
    ```java
       Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
       expect.toMatchSnapshot("Something");
    ```
   
Here is a JUnit5 example that does not use the JUnit5 extension

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.PropertyResolvingSnapshotConfig;
import au.com.origin.snapshots.SnapshotVerifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

// Notice we aren't using any framework extensions
public class CustomFrameworkExample {

  private static SnapshotVerifier snapshotVerifier;

  @BeforeAll
  static void beforeAll() {
    snapshotVerifier = new SnapshotVerifier(new PropertyResolvingSnapshotConfig(), CustomFrameworkExample.class);
  }

  @AfterAll
  static void afterAll() {
    snapshotVerifier.validateSnapshots();
  }

  @Test
  void shouldMatchSnapshotOne(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Hello World");
  }

}
```


## Resolving conflicting snapshot comparison via `*.snap.debug`

Often your IDE has an excellent file comparison tool.

- A `*.snap.debug` file will be created alongside your `*.snap` file when a conflict occurs.
- You can then use your IDE tooling to compare the two files.
- `*.snap.debug` is deleted automatically once the test passes.

**Note:** `*.snap.debug` files should never be checked into version control so consider adding it to your `.gitignore`

## snapshot.properties (required as of v2.4.0)

This file allows you to conveniently setup global defaults

|     key          |  Description                                                                                                   |
|------------------|----------------------------------------------------------------------------------------------------------------|
|serializer        | Class name of the [serializer](#supplying-a-custom-snapshotserializer), default serializer                     |
|serializer.{name} | Class name of the [serializer](#supplying-a-custom-snapshotserializer), accessible via `.serializer("{name}")` |
|comparator        | Class name of the [comparator](#supplying-a-custom-snapshotcomparator)                                         |
|reporters         | Comma separated list of class names to use as [reporters](#supplying-a-custom-snapshotreporter)                |
|snapshot-dir      | Name of sub-folder holding your snapshots                                                                      |
|output-dir        | Base directory of your test files (although it can be a different directory if you want)                       |
|ci-env-var        | Name of environment variable used to detect if we are running on a Build Server                                |

For example:

 ```text
serializer=au.com.origin.snapshots.serializers.ToStringSnapshotSerializer
serializer.json=au.com.origin.snapshots.serializers.DeterministicJacksonSnapshotSerializer
serializer.orderedJson=au.com.origin.snapshots.serializers.DeterministicJacksonSnapshotSerializer
comparator=au.com.origin.snapshots.comparators.PlainTextEqualsComparator
reporters=au.com.origin.snapshots.reporters.PlainTextSnapshotReporter
snapshot-dir=__snapshots__
output-dir=src/test/java
ci-env-var=CI
```

## Parameterized tests

In cases where the same test runs multiple times with different parameters you need to set the `scenario` and it must be
unique for each run

```java
expect.scenario(params).toMatchSnapshot("Something");
```

## Scenario Example

```groovy
package au.com.origin.snapshots.docs

import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

@EnableSnapshots
class SpockWithParametersExample extends Specification {

    // Note: "expect" will get injected later so can remain begin as null in the data-table 
    def 'Convert #scenario to uppercase'(def scenario, def value, Expect expect) {
        when: 'I convert to uppercase'
        String result = value.toUpperCase();
        then: 'Should convert letters to uppercase'
        // Check you snapshot against your output using a unique scenario
        expect.scenario(scenario).toMatchSnapshot(result)
        where:
        scenario | value | expect
        'letter' | 'a'   | null
        'number' | '1'   | null
    }
}
```

## Supplying a custom SnapshotSerializer

The serializer determines how a class gets converted into a string.

Currently, we support three different serializers

### Shipped with core

| Serializer                             | Description                                                                                                                 |
|----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| ToStringSnapshotSerializer             | uses the `toString()` method                                                                                                | 
| Base64SnapshotSerializer               | use for images or other binary sources that output a `byte[]`. The output is encoded to Base64                             |

### Shipped with Jackson plugin

| Serializer                             | Description                                                                                                                 |
|----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| JacksonSnapshotSerializer              | uses [jackson](https://github.com/FasterXML/jackson) to convert a class to a snapshot                                       |
| DeterministicJacksonSnapshotSerializer | extension of JacksonSnapshotSerializer that also orders Collections for situations where the order changes on multiple runs | 

Serializers are pluggable, so you can write you own by implementing the `SnapshotSerializer` interface.

Serializers are resolved in the following order.

- (method level) explicitly `expect(...).serializer(ToStringSerializer.class).toMatchSnapshot();` or via property
  file `expect(...).serializer("json").toMatchSnapshot();`
- (class level) explicitly `@UseSnapshotConfig` which gets read from the `getSerializer()` method
- (properties) implicitly via `snapshot.properties`

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
@UseSnapshotConfig(LowercaseToStringSnapshotConfig.class)
public class JUnit5ResolutionHierarchyExample {

  @Test
  public void aliasMethodTest(Expect expect) {
    expect
        .serializer("json") // <------ Using snapshot.properties
        .toMatchSnapshot(new TestObject());
  }

  @Test
  public void customSerializerTest(Expect expect) {
    expect
        .serializer(UppercaseToStringSerializer.class)  // <------ Using custom serializer
        .toMatchSnapshot(new TestObject());
  }

  // Read from LowercaseToStringSnapshotConfig defined on the class
  @Test
  public void lowercaseTest(Expect expect) {
    expect.toMatchSnapshot(new TestObject());
  }
}
```

### Example: HibernateSerializer

Sometimes the default serialization doesn't work for you. An example is Hibernate serialization where you get infinite
recursion on Lists/Sets.

You can supply any serializer you like Gson, Jackson or something else.

For example, the following will exclude the rendering of Lists without changing the source code to include `@JsonIgnore`
. This is good because you shouldn't need to add annotations to your source code for testing purposes only.

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.serializers.DeterministicJacksonSnapshotSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class HibernateSnapshotSerializer extends DeterministicJacksonSnapshotSerializer {

  @Override
  public void configure(ObjectMapper objectMapper) {
    super.configure(objectMapper);

    // Ignore Hibernate Lists to prevent infinite recursion
    objectMapper.addMixIn(List.class, IgnoreTypeMixin.class);
    objectMapper.addMixIn(Set.class, IgnoreTypeMixin.class);

    // Ignore Fields that Hibernate generates for us automatically
    objectMapper.addMixIn(BaseEntity.class, IgnoreHibernateEntityFields.class);
  }

  @JsonIgnoreType
  class IgnoreTypeMixin {
  }

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

## Supplying a custom SnapshotComparator

The comparator determines if two snapshots match.

Currently, we support one default comparator (`PlainTextEqualsComparator`) which uses string equals for comparison.

This should work for most cases. Custom implementations of `SnapshotComparator` can provide more advanced comparisons.

Comparators follow the same resolution order as Serializers

1. method
1. class
1. snapshot.properties

### Example: JsonObjectComparator

The default comparator may be too strict for certain types of data. For example, when comparing json objects, formatting
of the json string or the order of fields may not be of much importance during comparison. A custom comparator can help
in such cases.

For example, the following will convert a json string to a Map and then perform an equals comparison so that formatting
and field order are ignored.

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.comparators.SnapshotComparator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonObjectComparator implements SnapshotComparator {
  @Override
  public boolean matches(String snapshotName, String rawSnapshot, String currentObject) {
    return asObject(snapshotName, rawSnapshot).equals(asObject(snapshotName, currentObject));
  }

  @SneakyThrows
  private static Object asObject(String snapshotName, String json) {
    return new ObjectMapper().readValue(json.replaceFirst(snapshotName, ""), Object.class);
  }
}
```

## Supplying a custom SnapshotReporter

The reporter reports the details of comparison failures.

Currently, we support one default reporter (`PlainTextSnapshotReporter`) which uses assertj's DiffUtils to generate a
patch of the differences between two snapshots.

Custom reporters can be plugged in by implementing `SnapshotReporter`.

Reporters follow the same resolution order as Serializers and Comparators

### Example: JsonDiffReporter

For generating and reporting json diffs using other libraries like https://github.com/skyscreamer/JSONassert
a custom reporter can be created like the one below.

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SerializerType;
import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JsonAssertReporter implements SnapshotReporter {
  @Override
  public boolean supportsFormat(String outputFormat) {
    return SerializerType.JSON.name().equalsIgnoreCase(outputFormat);
  }

  @Override
  @SneakyThrows
  public void report(String snapshotName, String rawSnapshot, String currentObject) {
    JSONAssert.assertEquals(rawSnapshot, currentObject, JSONCompareMode.STRICT);
  }
}
```

## Supplying a custom SnapshotConfig

You can override the snapshot configuration easily using the `@UseSnapshotConfig` annotation

**JUnit5 Example**

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
// apply your custom snapshot configuration to this test class
@UseSnapshotConfig(LowercaseToStringSnapshotConfig.class)
public class CustomSnapshotConfigExample {

  @Test
  public void myTest(Expect expect) {
    expect.toMatchSnapshot("hello world");
  }
}
```

## Automatically updating a snapshot via `-PupdateSnapshot=filter`

Often - after analysing each snapshot and verifying it is correct, you will need to generate a new baseline for the
snapshots.

Note that you may need to do some Gradle trickery to make this visible to your actual tests

```groovy
test {
    systemProperty "updateSnapshot", project.getProperty("updateSnapshot")
}
```

Instead of deleting or manually modifying each snapshot you can pass `-PupdateSnapshot` which is equivalent to
the `--updateSnapshot` flag in [Jest](https://jestjs.io/docs/en/snapshot-testing#updating-snapshots)

#### Update all snapshots automatically

```
-PupdateSnapshot
```

#### Update selected snapshots only using `filter`

pass the class names you want to update to `filter`

```
-PupdateSnapshot=UserService,PermissionRepository
```

# Troubleshooting

**I'm seeing this error in my logs**

```
org/slf4j/LoggerFactory
java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory
```

Solution:
Add an SLF4J Provider such as`testImplementation("org.slf4j:slf4j-simple:2.0.0-alpha0")`

**My test source files are not in `src/test/java`**

Solution: Override `output-dir` in `snapshot.properties`

**I see the following error in JSON snapshots `java.lang.NoSuchFieldError: BINARY`**

Solution: This happened to me in a spring-boot app, I removed my jackson dependencies and relied on the ones from
spring-boot instead.

# Contributing

see `CONTRIBUTING.md`
