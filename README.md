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




#### How to install using [Maven](https://mvnrepository.com/artifact/io.github.json-snapshot/json-snapshot/1.0.17)



Add to your pom.xml dependencies section:

```xml
<dependency>
    <groupId>io.github.json-snapshot</groupId>
    <artifactId>json-snapshot</artifactId>
    <version>1.0.17</version>
</dependency>
```


#### Usage

```java
package com.example;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static io.github.jsonSnapshot.SnapshotMatcher.*;
import static io.github.jsonSnapshot.SnapshotUtils.*;
import io.github.jsonSnapshot.SnapshotCaptor;

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

        expect(extractArgs(fakeObject, "fakeMethod", new SnapshotCaptor(String.class), new SnapshotCaptor(Long.class), new SnapshotCaptor(List.class)))
                .toMatchSnapshot();
    }
    
    @Test // Snapshot arguments passed to mocked object support ignore of fields
    public void shouldExtractArgsFromFakeMethodWithComplexObject() {
        FakeObject fake = new FakeObject();
        fake.setId("idMock");
        fake.setName("nameMock");

        //With Ignore
        fakeObject.fakeMethodWithComplexObject(fake);
        Object fakeMethodWithComplexObjectWithIgnore = extractArgs(
                fakeObject, "fakeMethodWithComplexObject", 
                new SnapshotCaptor(Object.class, FakeObject.class, "name"));

        Mockito.reset(fakeObject);

        // Without Ignore of fields
        fakeObject.fakeMethodWithComplexObject(fake);
        Object fakeMethodWithComplexObjectWithoutIgnore = extractArgs(
                fakeObject, "fakeMethodWithComplexObject", 
                new SnapshotCaptor(Object.class, FakeObject.class));

        expect(fakeMethodWithComplexObjectWithIgnore, fakeMethodWithComplexObjectWithoutIgnore).toMatchSnapshot();
    }
    
    class FakeObject {
        
        private String id;

        private Integer value;

        private String name;

        void fakeMethod(String fakeName, Long fakeNumber, List<String> fakeList) {

        }
        
        void fakeMethodWithComplexObject(Object fakeObj) {
        
        }
        
        void setId(String id) {
            this.id = id;
        }
        
        void setName(String name) {
            this.name = name;
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

Whenever it runs again, the `expect` method argument will be automatically validated with the `.snap` file. That is why you should commit every `.snap` file created.


#### Inheritance

Test classes inheritance becames usefull with snapshot testing due to the fact that the assertions are variable following snasphots, instead of code. 
To make usage of this benefit you should be aware of the following:

Start SnapshotMatcher on child classes only:

```java
package com.example;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

public class SnapshotChildClassTest extends SnapshotSuperClassTest {

    @BeforeClass
    public static void beforeAll() {
        start();
    }

    @AfterClass
    public static void afterAll() {
        validateSnapshots();
    }
    
    @Override
    public String getName() {
        return "anyName";
    }
}
```

Super classes can have @Test defined, but you should make the class abstract.

```java
package com.example;

import org.junit.Test;

import static io.github.jsonSnapshot.SnapshotMatcher.expect;

public abstract class SnapshotSuperClassTest {

    public abstract String getName();

    @Test
    public void shouldMatchSnapshotOne() {
        expect(getName()).toMatchSnapshot();
    }

}
```
