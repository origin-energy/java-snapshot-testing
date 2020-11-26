[![Build Status](https://github.com/origin-energy/java-snapshot-testing/workflows/Build/badge.svg)](https://github.com/origin-energy/java-snapshot-testing/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.origin-energy/java-snapshot-testing-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.origin-energy/java-snapshot-testing-core)

# Java Snapshot Testing
- Inspired by [facebook's Jest framework](https://facebook.github.io/jest/docs/en/snapshot-testing.html)
- Fork of [json-snapshot.github.io](https://github.com/json-snapshot/json-snapshot.github.io)

## The testing framework loved by ~~lazy~~ productive devs
- Tired of needing to `assertThat(foo).isEqualTo("bar")` again & again?
- Are you just wanting to ensure you don't break - for example - REST interfaces
- Are you manually saving text files for verification in your tests?

**Want a better way?**
Then java-snapshot-testing might just be what you are looking for! 

## Advantages of Snapshot Testing
It's useful for deterministic tests. That is, running the same tests multiple times on a component that hasn't changed 
should produce the same results every time. You're responsible for making sure your generated snapshots do not include 
platform specific or other non-deterministic data. 

- Great for testing JSON interfaces ensuring you don't break clients
- Fast and easy to test
- Will implicitly test areas of your code you did not think about
- Great of testing dynamic objects

## Disadvantages of Snapshot Testing
- You need to ensure your test is deterministic for all fields (there are ways to ignore things like dates)
- Does not give great insight to why the snapshot failed
- Can be difficult to troll though large snapshot changes where you might only be interested in a small set of fields

## Installation [Maven](https://search.maven.org/search?q=java-snapshot-testing)

These docs are for the latest `-SNAPSHOT` version published to maven central.
Select the branch `release/X.X.X` matching your maven dependency to get correct documentation for your version.

Only if you want to integrate with non supported framework. [Show me how!](#custom-framework)
- [Core](https://search.maven.org/search?q=a:java-snapshot-testing-core)

We currently support:
- [JUnit4](https://search.maven.org/search?q=a:java-snapshot-testing-junit4)
- [JUnit5](https://search.maven.org/search?q=a:java-snapshot-testing-junit5)
- [Spock](https://search.maven.org/search?q=a:java-snapshot-testing-spock)

### Using the latest SNAPSHOT (excuse the pun)
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

## How does it work?
1. When a test runs for the first time, a `.snap` file is created in a `__snapshots__` subdirectory
1. On subsequent test runs, the `.snap` file is compared with the one produced by the test
1. If they don't match, the test fails and a `.snap.debug` with the conflict is created
1. It is then your job to decide if you have introduced a regression or intentionally changed the output (Use your IDE file comparison tools to compare the two files or refer to the terminal output)
1. If you have introduced a regression you will need to fix your code
1. If you have intentionally changed the output you can manually modify the `.snap` file to make it pass or delete it and it will be generated again from scratch
1. One you fix the test, the `*.snap.debug` file will get deleted

## What is a Snapshot?
A text representation of your java object (toString() or JSON).

**String snapshot example**
```java
// By default `.string()` is the default so not strictly required (unless you override the default!)
expect("hello world", "Hello world again!").string().toMatchSnapshot();
```
```text
au.com.example.company.HelloWorldTest.helloWorld=[
Hello world
Hello world again!
]
```

**JSON Snapshot Example**
```java
expect(userDto).json().toMatchSnapshot();
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
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Ensure you extend your test class with the SnapshotExtension
@ExtendWith(SnapshotExtension.class)
public class SnapshotExtensionUsedTest {

    @Test
    public void shouldUseExtension() {
        // Verify your snapshot
        SnapshotMatcher.expect("Hello World").toMatchSnapshot();
    }

    @Test
    public void exampleSnapshot() {
        SnapshotMatcher.expect("Hello World Again").toMatchSnapshot();
    }
}
```
## [JUnit 4](https://junit.org/junit4)
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
        SnapshotMatcher.expect("Hello World").toMatchSnapshot();
    }

    @Test
    public void shouldUseExtensionAgain() {
        SnapshotMatcher.expect("Hello World Again").toMatchSnapshot();
    }
}
```

## [Spock](http://spockframework.org/)
```groovy
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

// Ensure you enable snapshot testing support
@EnableSnapshots
class SpockExtensionUsedSpec extends Specification {
    def "Should use extension"() {
        when:
        SnapshotMatcher.expect("Hello World").toMatchSnapshot()

        then:
        true
    }
}
```

# Using an unsupported framework
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

## Resolving conflicting snapshot comparison via `*.snap.debug`
Often your IDE has an excellent file comparison tool.

- A `*.snap.debug` file will be created alongside your `*.snap` file when a conflict occurs.
- You can then use your IDE tooling to compare the two files.
- `*.snap.debug` is deleted automatically once the test passes.

**Note:** `*.snap.debug` files should never be checked into version control so consider adding it to your `.gitignore`

## Parameterized tests
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

Currently, we support three different serializers

| Serializer                             | Alias          | Description                                                                                                                 |
|----------------------------------------|----------------|-----------------------------------------------------------------------------------------------------------------------------|
| ToStringSnapshotSerializer (default)   | .string()      | uses the `toString()` method                                                                                                | 
| JacksonSnapshotSerializer              | .json()        | uses [jackson](https://github.com/FasterXML/jackson) to convert a class to a snapshot                                       |
| DeterministicJacksonSnapshotSerializer | .orderedJson() | extension of JacksonSnapshotSerializer that also orders Collections for situations where the order changes on multiple runs | 

Serializers are pluggable, so you can write you own by implementing the `SnapshotSerializer` interface.

Serializers are resolved in the following order.
- (method level) explicitly `expect(...).serializer(ToStringSerializer.class).toMatchSnapshot();`
- (class level) explicitly `@UseSnapshotConfig` which gets read from the `getSerializer()` method
- (global) implicitly via `SnapshotConfig` default for your test framework 

```java
@ExtendWith(SnapshotExtension.class)
@UseSnapshotConfig(LowercaseToStringSnapshotConfig.class)
public class SnapshotExtensionUsedTest {

    @Test
    public void aliasMethodTest() {
        expect(new TestObject())
                .orderedJson() // <------ Using alias() method
                .toMatchSnapshot();
    }

    @Test
    public void customSerializerTest() {
        expect(new TestObject())
                .serializer(UppercaseToStringSerializer.class)  // <------ Using custom serializer
                .toMatchSnapshot();
    }

    // Read from LowercaseToStringSnapshotConfig defined on the class
    @Test
    public void lowercaseTest() {
        expect(new TestObject()).toMatchSnapshot();
    }
}
```

### Example: HibernateSerializer
Sometimes the default serialization doesn't work for you. An example is Hibernate serialization where you get infinite recursion on Lists/Sets.

You can supply any serializer you like Gson, Jackson or something else.

For example,this following will exclude the rendering of Lists without changing the source code to include `@JsonIgnore`.
This is good because you shouldn't need to add annotations to your source code for testing purposes only.

```java
public class HibernateSnapshotSerializer implements SnapshotSerializer {

    private final JacksonSnapshotSerializer jacksonSerializer = new JacksonSnapshotSerializer() {
        @Override
        public void configure(ObjectMapper objectMapper) {
            super.configure(objectMapper);

            // Ignore Hibernate Lists to prevent infinite recursion
            objectMapper.addMixIn(List.class, IgnoreTypeMixin.class);

            // Ignore Fields that Hibernate generates for us automatically
            objectMapper.addMixIn(BaseEntity.class, IgnoreHibernateEntityFields.class);
        }
    };

    @Override
    public String apply(Object[] objects) {
        return jacksonSerializer.apply(objects);
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

        @JsonIgnore
        abstract Customer getCustomer();

        @JsonIgnore
        abstract Customer getAccount();
    }
}
```

## Supplying a custom SnapshotComparator
The comparator determines if two snapshots match.

Currently, we support one default comparator (`PlainTextEqualsComparator`) which uses string equals for comparison.

This should work for most cases. Custom implementations of `SnapshotComparator` can provide more advanced comparisons.

Comparators follow the same resolution order as Serializers
1. method 
2. class
3. global

### Example: JsonObjectComparator
The default comparator may be too strict for certain types of data.
For example, when comparing json objects, formatting of the json string or the order of fields 
may not be of much importance during comparison. A custom comparator can help in such cases. 

For example, the following will convert a json string to a Map and then perform an equals comparison
so that formatting and field order are ignored.

```java
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

Currently, we support one default reporter (`PlainTextSnapshotReporter`) which uses assertj's DiffUtils 
to generate a patch of the differences between two snapshots.

Custom reporters can be plugged in by implementing `SnapshotReporter`.

Reporters follow the same resolution order as Serializers and Comparators

### Example: JsonDiffReporter
For generating and reporting json diffs using other libraries like https://github.com/skyscreamer/JSONassert
a custom reporter can be created like the one below. 

```java
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
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
// apply your custom snapshot configuration to this test class
@UseSnapshotConfig(HibernateSnapshotConfig.class)
public class SnapshotExtensionUsedTest {

    @Test
    public void myTest() {
        // ...
    }
}
```

## Automatically updating a snapshot via `-PupdateSnapshot=filter`
Often - after analysing each snapshot and verifying it is correct, 
you will need to generate a new baseline for the snapshots.

Instead of deleting or manually modifying each snapshot you can pass `-PupdateSnapshot` which is equivalent to the `--updateSnapshot` flag in [Jest](https://jestjs.io/docs/en/snapshot-testing#updating-snapshots)

#### Update all snapshots automatically
```
-PupdateSnapshot
```

#### Update selected snapshots only using `filter`
pass the class names you want to update to `filter`
```
-PupdateSnapshot=UserService,PermissionRepository
```

### Changing the output directory
By default, output files are relative to `src/test/java` if you require a different directory structure
create a custom SnapshotConfig and override `getOutputDir()` as follows.

```java
class SnapshotConfig implements SnapshotConfig {

    @Override
    String getOutputDir() {
        return "src/integration/groovy";
    }

}
```

# Contributing

see `CONTRIBUTING.md`
