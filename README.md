#### Purpose of Snapshot Testing
Snapshots help figuring out whether the output of the modules covered by tests is changed, without doing tons of asserts!

#### When is it usefull?

It's usefull for deterministic tests. That is, running the same tests multiple times on a component that has not changed 
should produce the same results every time. You're responsible for making sure your generated snapshots do not include 
platform specific or other non-deterministic data. 

A Json Snapshot test does not assert Java types. You can continue doing that with any other testing framework.


Based on [facebook's Jest framework](https://facebook.github.io/jest/docs/en/snapshot-testing.html)

#### GitHub Repository
<a href="https://github.com/json-snapshot/json-snapshot.github.io"><img src="https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png" width="80"></a>




#### How to install using [Maven](https://mvnrepository.com/artifact/io.github.json-snapshot/json-snapshot/0.0.1)



Add to your pom.xml dependencies section:

```xml
<dependency>
    <groupId>io.github.json-snapshot</groupId>
    <artifactId>json-snapshot</artifactId>
    <version>1.0.3</version>
</dependency>
```


#### Usage

```java
package com.example;

import static io.github.jsonSnapshot.SnapshotMatcher.*;
import static io.github.jsonSnapshot.SnapshotUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class ExampleTest {

    @Mock
    private FakeObject fakeObject;


    @BeforeClass
    public static void beforeAll() {
        start();
    }
    
    @AfterClass
    public static void afterAll() {
        validateSnapshots();
    }
    
    @Test // Snapshot any object
    public void shouldShowSnapshotExample() {
        expect("<any type of object>").toMatchSnapshot();
    }

    @Test // Snapshot arguments passed to mocked object (from Mockito library)
    public void shouldExtractArgsFromMethod() {
        fakeObject.fakeMethod("test1", 1L, Arrays.asList("listTest1"));
        fakeObject.fakeMethod("test2", 2L, Arrays.asList("listTest1", "listTest2"));

        expect(extractArgs(fakeObject, "fakeMethod", String.class, Long.class, List.class)).toMatchSnapshot();
    }
    
    class FakeObject {
        private String id;

        private Integer value;

        private String name;

        void fakeMethod(String fakeName, Long fakeNumber, List<String> fakeList) {

        }
    }
}
```

When the test runs for the first time, the framework will create a snapshot file named `ExampleTest.snap` alongside with your test class. It should look like this:
```text
com.example.ExampleTest.shouldShowSnapshotExample=[
    "<any type of object>"
]


com.example.ExampleTest.shouldExtractArgsFromMethod=[
  {
    "FakeObject.fakeMethod": [
      {
        "arg0": "test1",
        "arg1": 1,
        "arg2": [
          "listTest1"
        ]
      },
      {
        "arg0": "test2",
        "arg1": 2,
        "arg2": [
          "listTest1",
          "listTest2"
        ]
      }
    ]
  }
]
```

Whenever it runs again, the `expect` method argument will be automatically validated with the `.snap` file. That is why you should commit every `.snap` file created.



