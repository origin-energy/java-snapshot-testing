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