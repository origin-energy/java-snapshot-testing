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
