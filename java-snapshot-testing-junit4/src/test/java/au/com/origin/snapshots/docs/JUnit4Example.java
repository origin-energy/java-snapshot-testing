package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit4.SnapshotRunner;
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
