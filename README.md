[![Build Status](https://github.com/origin-energy/java-snapshot-testing/workflows/build/badge.svg)](https://github.com/origin-energy/java-snapshot-testing/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.origin-energy/java-snapshot-testing-core/badge.svg)](https://search.maven.org/artifact/io.github.origin-energy/java-snapshot-testing-core/3.2.7/jar)

# Java Snapshot Testing
- Inspired by [facebook's Jest framework](https://facebook.github.io/jest/docs/en/snapshot-testing.html)

🎉 4.0.0 is out

## Upgrading
- Upgrade guide from 3.X to 4.X [here](https://github.com/origin-energy/java-snapshot-testing/discussions/94)
- Upgrade guide from 2.X to 3.X [here](https://github.com/origin-energy/java-snapshot-testing/discussions/73)
- Upgrade guide from 2.X-BETA to 2.X [here](https://github.com/origin-energy/java-snapshot-testing/discussions/58)

## The testing framework loved by ~~lazy~~ __productive__ devs

- Tired of needing to `assertThat(foo).isEqualTo("bar")` again & again?
- Are you just wanting to ensure you don't break - for example - REST interfaces
- Are you manually saving text files for verification in your tests?

**Want a better way?**
Then java-snapshot-testing might just be what you are looking for!

## Quick Start (Junit5 + Gradle example)

1. Add test dependencies

```groovy
// In this case we are using the JUnit5 testing framework
testImplementation 'io.github.origin-energy:java-snapshot-testing-junit5:4.+'

// slf4j logging implementation if you don't already have one
testImplementation("org.slf4j:slf4j-simple:2.0.0-alpha0")

// Optional: Many will want to serialize into JSON.  In this case you should also add the Jackson plugin
testImplementation 'io.github.origin-energy:java-snapshot-testing-plugin-jackson:4.+'
testImplementation 'com.fasterxml.jackson.core:jackson-core:2.11.3'
testImplementation 'com.fasterxml.jackson.core:jackson-databind:2.11.3'

// Optional: If you want Jackson to serialize Java 8 date/time types or Optionals you should also add the following dependencies
testRuntimeOnly 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.11.3'
testRuntimeOnly 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3'
```

2. Create `snapshot.properties` and configure your global settings. Be sure to set `output-dir` appropriately for your
   JVM language.

- /src/test/resources/snapshot.properties

 ```text
serializer=au.com.origin.snapshots.serializers.ToStringSnapshotSerializer
serializer.base64=au.com.origin.snapshots.serializers.Base64SnapshotSerializer
serializer.json=au.com.origin.snapshots.jackson.serializers.JacksonSnapshotSerializer
serializer.orderedJson=au.com.origin.snapshots.jackson.serializers.DeterministicJacksonSnapshotSerializer
comparator=au.com.origin.snapshots.comparators.PlainTextEqualsComparator
reporters=au.com.origin.snapshots.reporters.PlainTextSnapshotReporter
snapshot-dir=__snapshots__
output-dir=src/test/java
ci-env-var=CI
update-snapshot=none
```

3. Enable snapshot testing and write your first test

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

@ExtendWith({SnapshotExtension.class})
public class MyFirstSnapshotTest {

    private Expect expect;

    @SnapshotName("i_can_give_custom_names_to_my_snapshots")
    @Test
    public void toStringSerializationTest() {
        expect.toMatchSnapshot("Hello World");
    }

    @Test
    public void jsonSerializationTest() {
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


i_can_give_custom_names_to_my_snapshots=[
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
         // Optional java-snapshot-testing peer dependencies
         testRuntimeOnly 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.11.3'
         testRuntimeOnly 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3'
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
expect.toMatchSnapshot("Hello World");
```

```text
au.com.example.company.HelloWorldTest.helloWorld=[
Hello world
]
```

**JSON Snapshot Example**

```java
expect.serializer("json").toMatchSnapshot(userDto);
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

All frameworks allow injection of the `Expect expect` via instance variable or method argument. In cases where
parameterised tests are used, it's often better to use an instance variable in order to avoid conflicts with
the underlying data table. 

Note: Due to the above restriction, method argument injection is destined for removal in future versions.

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

  // Option 1: inject Expect as an instance variable
  private Expect expect;

  @Test
  public void myTest1() {
    // Verify your snapshot
    expect.toMatchSnapshot("Hello World");
  }

  // Option 2: inject Expect into the method signature
  @Test
  public void myTest2(Expect expect) {
    expect.toMatchSnapshot("Hello World Again");
  }
}
```

## [JUnit 4](https://junit.org/junit4)

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit4.SnapshotRunner;
import au.com.origin.snapshots.Expect;
import org.junit.Test;
import org.junit.runner.RunWith;

// Ensure you RunWith the SnapshotRunner
@RunWith(SnapshotRunner.class)
public class JUnit4Example {

  // Option 1: inject Expect as an instance variable
  private Expect expect;

  @SnapshotName("my first test")
  @Test
  public void myTest1() {
    // Verify your snapshot
    expect.toMatchSnapshot("Hello World");
  }

  @SnapshotName("my second test")
  @Test
  // Option 2: inject Expect into the method signature
  public void myTest2(Expect expect) {
    expect.toMatchSnapshot("Hello World Again");
  }
}
```

In order to run alongside another JUnit4 test runner such as `@RunWith(Parameterized.class)`, you need to use the 
Rule based configuration instead.

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit4.SnapshotClassRule;
import au.com.origin.snapshots.junit4.SnapshotRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class JUnit4RulesExample {

    @ClassRule
    public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();

    @Rule
    public SnapshotRule snapshotRule = new SnapshotRule(snapshotClassRule);

    private Expect expect;

    @SnapshotName("my first test")
    @Test
    public void myTest1() {
        expect.toMatchSnapshot("Hello World");
    }
}
```

See the [ParameterizedTest](https://github.com/origin-energy/java-snapshot-testing/blob/master/java-snapshot-testing-junit4/src/test/java/au/com/origin/snapshots/ParameterizedTest.java) for an example implementation

## [Spock](http://spockframework.org/)

```groovy
package au.com.origin.snapshots.docs

import au.com.origin.snapshots.annotations.SnapshotName
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

import au.com.origin.snapshots.Expect

// Ensure you enable snapshot testing support
@EnableSnapshots
class SpockExample extends Specification {

    // Option 1: inject Expect as an instance variable
    private Expect expect

    // With spock tests you should always use @SnapshotName - otherwise they become coupled to test order
    @SnapshotName("should_use_extension")
    def "Should use extension"() {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }

    @SnapshotName("should_use_extension_as_method_argument")
    // Option 2: inject Expect into the method signature
    def "Should use extension as method argument"(Expect expect) {
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
       Expect expect = Expect.of(snapshotVerifier, testMethod);
       expect.toMatchSnapshot("Something");
    ```
   
Here is a JUnit5 example that does not use the JUnit5 extension

```java
package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.SnapshotVerifier;
import au.com.origin.snapshots.config.PropertyResolvingSnapshotConfig;
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

## Supplying your own snapshot name via @SnapshotName
By default, snapshots use the full method name as the identifier 
For example
```text
au.com.origin.snapshots.docs.MyFirstSnapshotTest.helloWorldTest=[
Hello World
]
```

This strategy has a number of problems
- it's long and unwieldy
- if the method name or class name changes, your tests become orphans
- The Spock framework tests use a generated method name that is based on index (Spock tests should always use `@SnapshotName`)

You can supply a more meaningful name to your snapshot using `@SnapshotName("your_custom_name")`
This will generate as follows
```
your_custom_name=[
Hello World
]
```

Much more concise and not affected by class name or method name refactoring.

## Resolving conflicting snapshot comparison via `*.snap.debug`

Often your IDE has an excellent file comparison tool.

- A `*.snap.debug` file will be created alongside your `*.snap` file when a conflict occurs.
- You can then use your IDE tooling to compare the two files.
- `*.snap.debug` is deleted automatically once the test passes.

**Note:** `*.snap.debug` files should never be checked into version control so consider adding it to your `.gitignore`

## snapshot.properties (required as of v2.4.0)

This file allows you to conveniently setup global defaults

|     key          |  Description                                                                                                                                                       |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|serializer        | Class name of the [serializer](#supplying-a-custom-snapshotserializer), default serializer                                                                         |
|serializer.{name} | Class name of the [serializer](#supplying-a-custom-snapshotserializer), accessible via `.serializer("{name}")`                                                     |
|comparator        | Class name of the [comparator](#supplying-a-custom-snapshotcomparator)                                                                                             |
|comparator.{name} | Class name of the [comparator](#supplying-a-custom-snapshotcomparator), accessible via `.comparator("{name}")`                                                     |
|reporters         | Comma separated list of class names to use as [reporters](#supplying-a-custom-snapshotreporter)                                                                    |
|reporters.{name}  | Comma separated list of class names to use as [reporters](#supplying-a-custom-snapshotreporter), accessible via `.reporters("{name}")`                             |
|snapshot-dir      | Name of sub-folder holding your snapshots                                                                                                                          |
|output-dir        | Base directory of your test files (although it can be a different directory if you want)                                                                           |
|ci-env-var        | Name of environment variable used to detect if we are running on a Build Server                                                                                    |
|update-snapshot   | Similar to `--updateSnapshot` in [Jest](https://jestjs.io/docs/en/snapshot-testing#updating-snapshots) <br/>[all]=update all snapsohts<br/>[none]=update no snapshots<br/>[MyTest1,MyTest2]=update snapshots in these classes only<br/><br/>*Note: must be set to [none] on CI |

For example:

 ```text
serializer=au.com.origin.snapshots.serializers.ToStringSnapshotSerializer
serializer.base64=au.com.origin.snapshots.serializers.Base64SnapshotSerializer
serializer.json=au.com.origin.snapshots.jackson.serializers.JacksonSnapshotSerializer
serializer.orderedJson=au.com.origin.snapshots.jackson.serializers.DeterministicJacksonSnapshotSerializer
comparator=au.com.origin.snapshots.comparators.PlainTextEqualsComparator
reporters=au.com.origin.snapshots.reporters.PlainTextSnapshotReporter
snapshot-dir=__snapshots__
output-dir=src/test/java
ci-env-var=CI
update-snapshot=none
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
import au.com.origin.snapshots.annotations.SnapshotName
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

@EnableSnapshots
class SpockWithParametersExample extends Specification {

    private Expect expect

    @SnapshotName("convert_to_uppercase")
    def 'Convert #scenario to uppercase'() {
        when: 'I convert to uppercase'
        String result = value.toUpperCase();
        then: 'Should convert letters to uppercase'
        // Check you snapshot against your output using a unique scenario
        expect.scenario(scenario).toMatchSnapshot(result)
        where:
        scenario | value
        'letter' | 'a'
        'number' | '1'
    }
}
```

## Supplying a custom SnapshotSerializer

The serializer determines how a class gets converted into a string.

Serializers are pluggable, so you can write you own by implementing the `SnapshotSerializer` interface.

Currently, we support the following serializers. 

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

Serializers are resolved in the following order.

- (method level) explicitly `expect.serializer(ToStringSerializer.class).toMatchSnapshot(...);` or via property
  file `expect.serializer("json").toMatchSnapshot(...);`
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

    private Expect expect;

    @Test
    public void aliasMethodTest() {
        expect
                .serializer("json") // <------ Using snapshot.properties
                .toMatchSnapshot(new TestObject());
    }

    @Test
    public void customSerializerTest() {
        expect
                .serializer(UppercaseToStringSerializer.class)  // <------ Using custom serializer
                .toMatchSnapshot(new TestObject());
    }

    // Read from LowercaseToStringSnapshotConfig defined on the class
    @Test
    public void lowercaseTest() {
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

import au.com.origin.snapshots.jackson.serializers.DeterministicJacksonSnapshotSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonObjectComparator implements SnapshotComparator {
    @Override
    public boolean matches(Snapshot previous, Snapshot current) {
        return asObject(previous.getName(), previous.getBody()).equals(asObject(current.getName(), current.getBody()));
    }

    @SneakyThrows
    private static Object asObject(String snapshotName, String json) {
        return new ObjectMapper().readValue(json.replaceFirst(snapshotName + "=", ""), Object.class);
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

import au.com.origin.snapshots.Snapshot;
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
    public void report(Snapshot previous, Snapshot current) {
        JSONAssert.assertEquals(previous.getBody(), current.getBody(), JSONCompareMode.STRICT);
    }
}
```

## Snapshot Headers
You can add metadata to your snapshots via headers. Headers can be used by Serializers, Comparators & Reporters
to help interrogate the snapshot.

Custom Serializers can also inject default headers as needed.

Example of injecting a header manually
```java
String obj = "hello"
expect
    .header("className", obj.getClass().getName())
    .header("foo", "bar")
    .toMatchSnapshot(obj);
```

Snapshot output
```text
au.com.origin.snapshots.SnapshotHeaders.canAddHeaders={
  "className": "java.lang.String",
  "foo": "bar"
}[
hello
]
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
@UseSnapshotConfig(LowercaseToStringSnapshotConfig.class)
public class JUnit5ResolutionHierarchyExample {

    private Expect expect;

    @Test
    public void aliasMethodTest() {
        expect
                .serializer("json") // <------ Using snapshot.properties
                .toMatchSnapshot(new TestObject());
    }

    @Test
    public void customSerializerTest() {
        expect
                .serializer(UppercaseToStringSerializer.class)  // <------ Using custom serializer
                .toMatchSnapshot(new TestObject());
    }

    // Read from LowercaseToStringSnapshotConfig defined on the class
    @Test
    public void lowercaseTest() {
        expect.toMatchSnapshot(new TestObject());
    }
}
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
