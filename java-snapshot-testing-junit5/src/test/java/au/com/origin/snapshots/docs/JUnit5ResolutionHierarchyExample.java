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