# json-snapshot
Snapshot Testing for Java. [Understand the hype!](https://facebook.github.io/jest/docs/en/snapshot-testing.html)

#### How to install using Maven

Add to your pom.xml dependencies section:

```xml
<dependency>
  <groupId>com.github.andrebonna</groupId>
  <artifactId>json-snapshot</artifactId>
  <version>0.0.2</version>
</dependency>
```


#### Usage

```java
package com.example;

import com.github.andrebonna.jsonSnapshot.*;

public class ExampleTest {
    @BeforeClass
    public static void beforeAll() {
        SnapshotMatcher.start();
    }
    
    @AfterClass
    public static void afterAll() {
        SnapshotMatcher.validateSnapshots();
    }
    
    @Test
    public void shouldShowSnapshotExample() {
        SnapshotMatcher.expect("<any type of object>").toMatchSnapshot();
    }
}
```

When the test runs for the first time, the framework will create a snapshot file named `ExampleTest.snap` alongside with your test class. It should look like this:
```text
com.example.ExampleTest| with |shouldShowSnapshotExample=[
  "<any type of object>"
]
```

Whenever it runs again, the `expect` method argument will be automatically validated with the `.snap` file. That is why you should commit every `.snap` file created.



